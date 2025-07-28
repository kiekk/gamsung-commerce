package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductLikeCountEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductLikeCountJpaRepository : JpaRepository<ProductLikeCountEntity, Long> {
    fun findByProductId(productId: Long): ProductLikeCountEntity?
}
