package com.loopers.domain.product

interface ProductLikeCountRepository {
    fun findByProductId(productId: Long): ProductLikeCountEntity?

    fun save(productLikeCountEntity: ProductLikeCountEntity): ProductLikeCountEntity
}
