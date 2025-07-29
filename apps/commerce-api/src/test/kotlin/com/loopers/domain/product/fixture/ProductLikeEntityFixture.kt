package com.loopers.domain.product.fixture

import com.loopers.domain.product.ProductLikeEntity

class ProductLikeEntityFixture {
    private var userId: Long = 1L
    private var productId: Long = 1L

    companion object {
        fun aProductLike(): ProductLikeEntityFixture = ProductLikeEntityFixture()
    }

    fun userId(userId: Long): ProductLikeEntityFixture = apply { this.userId = userId }

    fun productId(productId: Long): ProductLikeEntityFixture = apply { this.productId = productId }

    fun build(): ProductLikeEntity = ProductLikeEntity(
        userId,
        productId,
    )
}
