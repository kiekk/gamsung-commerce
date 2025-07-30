package com.loopers.infrastructure.productlike

import com.loopers.domain.productlike.ProductLikeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductLikeJpaRepository : JpaRepository<ProductLikeEntity, Long> {
    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean

    fun findByUserId(userId: Long): List<ProductLikeEntity>

    fun deleteByUserIdAndProductId(userId: Long, productId: Long)
}
