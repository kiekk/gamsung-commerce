package com.loopers.domain.productlike.fixture

import com.loopers.domain.productlike.ProductLikeCountEntity

class ProductLikeCountEntityFixture {
    private var productId: Long = 1L
    private var productLikeCount: Int = 0

    companion object {
        fun aProductLikeCount(): ProductLikeCountEntityFixture = ProductLikeCountEntityFixture()
    }

    fun productId(productId: Long): ProductLikeCountEntityFixture = apply { this.productId = productId }

    fun productLikeCount(productLikeCount: Int): ProductLikeCountEntityFixture =
        apply { this.productLikeCount = productLikeCount }

    fun build(): ProductLikeCountEntity = ProductLikeCountEntity(
        productId,
        productLikeCount,
    )
}
