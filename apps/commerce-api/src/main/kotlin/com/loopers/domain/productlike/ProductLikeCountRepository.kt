package com.loopers.domain.productlike

interface ProductLikeCountRepository {
    fun findByProductId(productId: Long): ProductLikeCountEntity?

    fun findOptimisticLockedByProductId(productId: Long): ProductLikeCountEntity?

    fun findPessimisticLockedByProductId(productId: Long): ProductLikeCountEntity?

    fun save(productLikeCountEntity: ProductLikeCountEntity): ProductLikeCountEntity
}
