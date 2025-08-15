package com.loopers.support.cache

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
private class CacheRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) : CacheRepository {

    override fun <T> get(cacheKey: CacheKey, clazz: Class<T>): T? {
        val value = redisTemplate.opsForValue().get(cacheKey.fullKey()) ?: return null
        return runCatching { DataSerializer.deserialize(value, clazz) }.getOrNull()
    }

    override fun <T> set(cacheKey: CacheKey, value: T) {
        runCatching {
            DataSerializer.serialize(value)?.let {
                redisTemplate.opsForValue().set(cacheKey.fullKey(), it, cacheKey.ttl)
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

}
