package com.loopers.support.cache

import com.loopers.support.cache.dto.ScoreRankDto
import java.time.Duration

interface CacheRepository {
    fun <T> get(cacheKey: CacheKey, clazz: Class<T>): T?

    fun <T> set(cacheKey: CacheKey, value: T)

    fun <T> set(key: String, value: T, ttl: Duration)

    fun evict(key: String)

    fun findTopRankByScoreDesc(cacheKey: CacheKey, offset: Long, size: Int): Map<String, ScoreRankDto>

    fun findRank(cacheKey: CacheKey, item: String): Long?

    fun getTotalCount(cacheKey: CacheKey): Long
}
