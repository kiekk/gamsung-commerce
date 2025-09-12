package com.loopers.support.cache.productrank

import com.loopers.support.cache.CacheKey
import com.loopers.support.cache.CacheNames
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ProductRankCacheKeyGenerator {

    fun generate(date: LocalDateTime): CacheKey {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHH")
        val formattedDate = formatter.format(date)
        return CacheKey(
            CacheNames.PRODUCT_RANK_ALL_KEY_PREFIX,
            formattedDate,
            // ttl 2시간 설정
            Duration.ofHours(2),
        )
    }

    fun generate(date: LocalDate): CacheKey {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val formattedDate = formatter.format(date)
        return CacheKey(
            CacheNames.PRODUCT_RANK_ALL_KEY_PREFIX,
            formattedDate,
            // ttl 2일로 설정
            Duration.ofDays(2),
        )
    }
}
