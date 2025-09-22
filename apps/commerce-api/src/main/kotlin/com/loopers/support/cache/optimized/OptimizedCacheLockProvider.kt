package com.loopers.support.cache.optimized

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class OptimizedCacheLockProvider(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun lock(key: String): Boolean {
        return redisTemplate.opsForValue().setIfAbsent(
            generateLockKey(key),
            "",
            LOCK_TTL,
        ) == true
    }

    fun unlock(key: String): Boolean {
        return redisTemplate.delete(generateLockKey(key))
    }

    private fun generateLockKey(key: String): String {
        return KEY_FORMAT + key
    }

    companion object {
        private const val KEY_FORMAT = "optimized-cache-lock::"
        private val LOCK_TTL: Duration = Duration.ofSeconds(3)
    }
}
