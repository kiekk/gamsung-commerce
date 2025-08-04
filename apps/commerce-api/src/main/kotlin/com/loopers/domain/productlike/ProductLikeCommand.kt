package com.loopers.domain.productlike

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
