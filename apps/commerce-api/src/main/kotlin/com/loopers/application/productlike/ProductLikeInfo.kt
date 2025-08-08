package com.loopers.application.productlike

import com.loopers.domain.product.ProductEntity
import com.loopers.support.enums.product.ProductStatusType

class ProductLikeInfo {
    data class UserLikeProductDetail(
        val productId: Long,
        val productName: String,
        val description: String?,
        val productPrice: Long,
        val productStatus: ProductStatusType,
    ) {
        companion object {
            fun from(productEntity: ProductEntity): UserLikeProductDetail {
                return UserLikeProductDetail(
                    productEntity.id,
                    productEntity.name,
                    productEntity.description,
                    productEntity.price.value,
                    productEntity.status,
                )
            }
        }
    }
}
