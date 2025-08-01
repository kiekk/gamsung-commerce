package com.loopers.domain.productlike.fixture

import com.loopers.domain.productlike.ProductLikeEntity

class ProductLikeEntityFixture {
    private var productId: Long = 1L
    private var userId: Long = 1L

    companion object {
        fun aProductLike(): ProductLikeEntityFixture = ProductLikeEntityFixture()
    }

    fun productId(productId: Long): ProductLikeEntityFixture = apply { this.productId = productId }

    fun userId(userId: Long): ProductLikeEntityFixture = apply { this.userId = userId }

    fun build(): ProductLikeEntity = ProductLikeEntity(
        userId,
        productId,
    )
}
