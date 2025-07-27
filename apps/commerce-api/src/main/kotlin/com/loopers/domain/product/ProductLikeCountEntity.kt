package com.loopers.domain.product

import com.loopers.domain.BaseEntity

class ProductLikeCountEntity(
    val productId: Long,
    var productLikeCount: Int = 0,
) : BaseEntity() {

    init {
        require(productLikeCount >= 0) { "좋아요 수는 0 이상이어야 합니다." }
    }

    fun increaseProductLikeCount() {
        productLikeCount++
    }

    fun decreaseProductLikeCount() {
        if (productLikeCount > 0) productLikeCount--
    }
}
