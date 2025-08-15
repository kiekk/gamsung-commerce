package com.loopers.support.cache

import java.time.Duration

object CacheNames {
    const val PRODUCT_LIST_V1  = "product:list:v1:"
    const val PRODUCT_DETAIL_V1 = "product:detail:v1:"
    const val BRAND_DETAIL_V1 = "brand:detail:v1:"
}

class CacheKey(
    val prefix: String,
    val key: String,
    val ttl: Duration = Duration.ofSeconds(10), // TTL 10초는 실제 운영에 적용하기에는 매우 짧지만 테스용으로 설정
) {

    fun fullKey(): String {
        return "$prefix$key"
    }

    override fun toString(): String {
        return "CacheKey(prefix='$prefix', key='$key', ttl=$ttl)"
    }

}
