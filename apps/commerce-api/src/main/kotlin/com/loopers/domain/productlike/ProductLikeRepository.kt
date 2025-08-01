package com.loopers.domain.productlike

interface ProductLikeRepository {
    fun create(productLike: ProductLikeEntity): ProductLikeEntity

    fun deleteByUserIdAndProductId(userId: Long, productId: Long)

    fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean

    fun findByUserId(userId: Long): List<ProductLikeEntity>
}
