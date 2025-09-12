package com.loopers.support.cache

import DataSerializer
import org.slf4j.LoggerFactory.getLogger
import org.springframework.data.redis.core.DefaultTypedTuple
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.random.Random

@Component
private class CacheRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) : CacheRepository {

    private val log = getLogger(this::class.java)

    override fun <T> get(cacheKey: CacheKey, clazz: Class<T>): T? {
        val value = redisTemplate.opsForValue().get(cacheKey.fullKey()) ?: return null
        return runCatching { DataSerializer.deserialize(value, clazz) }.getOrNull()
    }

    override fun <T> set(cacheKey: CacheKey, value: T) {
        runCatching {
            val jitteredTtl = jitteredTtl(cacheKey.ttl)
            log.info(
                "[jitteredTtl] key: {}, baseTTL: {}, jitteredTTL: {}",
                cacheKey.fullKey(),
                cacheKey.ttl.toMillis(),
                jitteredTtl.toMillis(),
            )
            DataSerializer.serialize(value)?.let {
                redisTemplate.opsForValue().set(cacheKey.fullKey(), it, jitteredTtl)
            }
        }.onFailure { e ->
            throw RuntimeException("Failed to serialize and set value for key: ${cacheKey.prefix}", e)
        }
    }

    override fun evict(key: String) {
        runCatching {
            redisTemplate.delete(key)
        }.onFailure { e ->
            throw RuntimeException("Failed to delete key: $key", e)
        }
    }

    override fun zIncrBy(cacheKey: CacheKey, productId: Long, score: Double) {
        log.info("[CacheRedisRepository.zIncrBy] cacheKey: {}, productId: {}, score: {}", cacheKey, productId, score)
        redisTemplate.opsForZSet().incrementScore(cacheKey.fullKey(), productId.toString(), score)
        if (redisTemplate.getExpire(cacheKey.fullKey()) == -1L) {
            log.info("[CacheRedisRepository.zIncrBy] Setting TTL for key: {}", cacheKey.ttl)
            redisTemplate.expire(cacheKey.fullKey(), cacheKey.ttl)
        }
    }

    override fun findTopRankByScoreDesc(cacheKey: CacheKey, offset: Long, size: Int): Map<String, Double> {
        log.info("[CacheRedisRepository.findTopRankByScoreDesc] cacheKey: {}, offset: {}, size: {}", cacheKey, offset, size)
        val tuples = redisTemplate.opsForZSet()
            .reverseRangeWithScores(cacheKey.fullKey(), offset, offset + size - 1)
            ?: return emptyMap()
        return tuples.mapIndexed { _, tuple ->
            tuple.value!! to tuple.score!!
        }.toMap()
    }

    override fun zAddAll(cacheKey: CacheKey, normalizedTuples: Set<DefaultTypedTuple<String>>) {
        log.info("[CacheRedisRepository.zAddAll] cacheKey: {}, normalizedTuples size: {}", cacheKey, normalizedTuples.size)
        redisTemplate.opsForZSet().add(cacheKey.fullKey(), normalizedTuples)
        if (redisTemplate.getExpire(cacheKey.fullKey()) == -1L) {
            log.info("[CacheRedisRepository.zAddAll] Setting TTL for key: {}", cacheKey.ttl)
            redisTemplate.expire(cacheKey.fullKey(), cacheKey.ttl)
        }
    }

    private fun jitteredTtl(baseTTL: Duration): Duration {
        val baseTTLMs = baseTTL.toMillis()
        val delta = (baseTTLMs * JITTER_PERCENTAGE).toLong() // 지터 최대 폭

        val offset = Random.nextLong(-delta, delta + 1)

        // MINIMUM_TTL_MINUTES으로 다시 계산, 5지만 5분으로 계산해야함
        val jittered = (baseTTLMs + offset).coerceAtLeast(MINIMUM_TTL_MINUTES.toMillis())

        return Duration.ofMillis(jittered)
    }

    companion object {
        private const val JITTER_PERCENTAGE = 0.15 // ±15%의 지터를 추가하여 TTL을 설정합니다.
        private val MINIMUM_TTL_MINUTES = Duration.ofMinutes(5) // 최소 TTL은 5분으로 설정합니다.
    }
}
