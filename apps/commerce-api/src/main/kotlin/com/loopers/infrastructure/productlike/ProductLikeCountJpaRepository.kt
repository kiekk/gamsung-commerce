package com.loopers.infrastructure.productlike

import com.loopers.domain.productlike.ProductLikeCountEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductLikeCountJpaRepository : JpaRepository<ProductLikeCountEntity, Long> {
    fun findByProductId(productId: Long): ProductLikeCountEntity?
}
