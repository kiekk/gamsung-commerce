package com.loopers.application.product

import com.loopers.domain.productlike.ProductLikeCommand

class ProductLikeCriteria {
    data class Like(
        val userId: Long,
        val productId: Long,
    ) {
        fun toCommand(): ProductLikeCommand.Like {
            return ProductLikeCommand.Like(
                userId,
                productId,
            )
        }
    }

    data class Unlike(
        val userId: Long,
        val productId: Long,
    ) {
        fun toCommand(): ProductLikeCommand.Unlike {
            return ProductLikeCommand.Unlike(
                userId,
                productId,
            )
        }
    }
}
