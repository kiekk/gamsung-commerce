package com.loopers.support.cache

import com.loopers.support.cache.dto.ScoreRankDto

interface CacheRepository {
    fun <T> get(cacheKey: CacheKey, clazz: Class<T>): T?

    fun <T> set(cacheKey: CacheKey, value: T)

    fun evict(key: String)

    fun findTopRankByScoreDesc(cacheKey: CacheKey, offset: Long, size: Int): Map<String, ScoreRankDto>

    fun findRank(cacheKey: CacheKey, item: String): Long?

    fun getTotalCount(cacheKey: CacheKey): Long
}
