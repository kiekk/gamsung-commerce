package com.loopers.application.productlike

class ProductLikeCriteria {
    data class Like(
        val username: String,
        val productId: Long,
    )

    data class Unlike(
        val username: String,
        val productId: Long,
    )
}
