package com.loopers.support.cache

import org.springframework.data.redis.core.DefaultTypedTuple

interface CacheRepository {
    fun <T> get(cacheKey: CacheKey, clazz: Class<T>): T?

    fun <T> set(cacheKey: CacheKey, value: T)

    fun evict(key: String)

    fun zIncrBy(cacheKey: CacheKey, productId: Long, score: Double)

    fun findTopRankByScoreDesc(cacheKey: CacheKey, offset: Long, size: Int): Map<String, Double>

    fun zAddAll(cacheKey: CacheKey, normalizedTuples: Set<DefaultTypedTuple<String>>)
}
