package com.loopers.application.product

import com.loopers.domain.product.ProductLikeEntity

class ProductLikeCommand {
    data class Like(
        val userId: Long,
        val productId: Long,
    ) {
        fun toEntity(): ProductLikeEntity {
            return ProductLikeEntity(
                userId,
                productId,
            )
        }
    }

    data class Unlike(
        val userId: Long,
        val productId: Long,
    ) {
        fun toEntity(): ProductLikeEntity {
            return ProductLikeEntity(
                userId,
                productId,
            )
        }
    }
}
