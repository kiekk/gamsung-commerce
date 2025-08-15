package com.loopers.support.cache.policy

import com.loopers.support.cache.CacheKey
import com.loopers.support.cache.CacheNames
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Duration

object ProductListCachePolicy {
    private val ALLOWED_SORT_PROPERTIES = setOf("name", "price", "createdAt", "likeCount")

    // page 0,1만 캐시 & 정렬 허용 체크
    fun isCacheable(pageable: Pageable): Boolean {
        if (pageable.pageNumber !in 0..1) return false
        val sortAllowed = pageable.sort == Sort.unsorted() ||
                pageable.sort.map { it.property }.all { it in ALLOWED_SORT_PROPERTIES }
        return sortAllowed
    }

    fun normalizeSort(sort: Sort): String =
        if (sort.isUnsorted) "unsorted"
        else sort.joinToString(",") { "${it.property}:${if (it.isAscending) "asc" else "desc"}" }

    // SHA-256으로 compact key 구성
    private fun sha256(s: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val dig = md.digest(s.toByteArray())
        return BigInteger(1, dig).toString(16).padStart(64, '0')
    }

    /**
     * CacheKey 생성 (TTL은 테스트에선 10초 기본, 운영은 적절히 조정)
     */
    fun buildCacheKey(
        pageable: Pageable,
        ttl: Duration? = null,
    ): CacheKey {
        val sort = normalizeSort(pageable.sort)
        val base = "p=${pageable.pageNumber}|s=${pageable.pageSize}|sort=$sort"
        val digest = sha256(base)
        return ttl
            ?.let { CacheKey(CacheNames.PRODUCT_LIST_V1, digest, it) }
            ?: CacheKey(CacheNames.PRODUCT_LIST_V1, digest)
    }
}
