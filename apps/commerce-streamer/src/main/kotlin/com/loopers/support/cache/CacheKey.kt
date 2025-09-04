package com.loopers.support.cache

import java.time.Duration

object CacheNames {
    const val PRODUCT_DETAIL_V1 = "product:detail:v1:"
    const val PRODUCT_LIKE_COUNT_V1 = "product:like-count:v1:"
    const val BRAND_DETAIL_V1 = "brand:detail:v1:"
}

class CacheKey(
    val prefix: String,
    val key: String,
    // Jitter 확인을 위해 10s -> 10m로 변경
    val ttl: Duration = Duration.ofMinutes(10),
) {

    fun fullKey(): String {
        return "$prefix$key"
    }

    override fun toString(): String {
        return "CacheKey(prefix='$prefix', key='$key', ttl=$ttl)"
    }
}
