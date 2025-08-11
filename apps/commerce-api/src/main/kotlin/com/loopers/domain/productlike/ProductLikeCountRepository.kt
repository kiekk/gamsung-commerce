package com.loopers.domain.productlike

interface ProductLikeCountRepository {
    fun findByProductId(productId: Long): ProductLikeCountEntity?

    fun findByProductIdWithOptimisticLock(productId: Long): ProductLikeCountEntity?

    fun findByProductIdWithPessimisticLock(productId: Long): ProductLikeCountEntity?

    fun save(productLikeCountEntity: ProductLikeCountEntity): ProductLikeCountEntity
}
