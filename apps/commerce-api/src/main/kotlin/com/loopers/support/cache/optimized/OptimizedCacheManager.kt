package com.loopers.support.cache.optimized

import DataSerializer
import com.loopers.support.cache.jitter.JitteredTTLCalculator
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class OptimizedCacheManager(
    private val redisTemplate: RedisTemplate<String, String>,
    private val optimizedCacheLockProvider: OptimizedCacheLockProvider,
) {

    private val log = LoggerFactory.getLogger(OptimizedCacheManager::class.java)

    @Throws(Throwable::class)
    fun process(
        type: String, ttlSeconds: Long, args: Array<Any?>, returnType: Class<*>,
        originDataSupplier: OptimizedCacheOriginDataSupplier<*>,
    ): Any? {
        val key = generateKey(type, args)

        log.info("[OptimizedCache] process - key: $key")
        val cachedData = redisTemplate.opsForValue()[key]
        if (cachedData == null) {
            log.info("[OptimizedCache] cache miss - key: $key")
            return refresh(originDataSupplier, key, ttlSeconds)
        }

        val optimizedCache = DataSerializer.deserialize(cachedData, OptimizedCache::class.java)
        if (optimizedCache == null) {
            log.info("[OptimizedCache] optimizedCache null - key: $key")
            return refresh(originDataSupplier, key, ttlSeconds)
        }

        if (!optimizedCache.isExpired()) {
            log.info("[OptimizedCache] cache hit - key: $key, optimizedCache: $optimizedCache")
            return optimizedCache.parseData(returnType)
        }

        if (!optimizedCacheLockProvider.lock(key)) {
            log.info("[OptimizedCache] lock fail - key: $key")
            return optimizedCache.parseData(returnType)
        }

        try {
            log.info("[OptimizedCache] cache refresh - key: $key, optimizedCache: $optimizedCache")
            return refresh(originDataSupplier, key, ttlSeconds)
        } finally {
            optimizedCacheLockProvider.unlock(key)
        }
    }

    @Throws(Exception::class)
    private fun refresh(originDataSupplier: OptimizedCacheOriginDataSupplier<*>, key: String, ttlSeconds: Long): Any? {
        val result = originDataSupplier.get()
        // jittered TTL 적용
        val jitteredTTL = JitteredTTLCalculator.jitteredTtl(Duration.ofSeconds(ttlSeconds))
        val optimizedCacheTTL = OptimizedCacheTTL.of(jitteredTTL)
        val optimizedCache = OptimizedCache.of(result, optimizedCacheTTL.logicalTTL)
        log.info("[OptimizedCache] refresh - key: $key, optimizedCache: $optimizedCache, optimizedCacheTTL: $optimizedCacheTTL")

        redisTemplate.opsForValue()
            .set(
                key,
                DataSerializer.serialize(optimizedCache),
                optimizedCacheTTL.physicalTTL,
            )

        return result
    }

    private fun generateKey(prefix: String?, args: Array<Any?>): String {
        return prefix + args.joinToString(DELIMITER)
    }

    companion object {
        private const val DELIMITER = ":"
    }
}
