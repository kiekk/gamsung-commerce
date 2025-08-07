package com.loopers.interfaces.api.product

import com.loopers.application.product.ProductCriteria
import com.loopers.application.product.ProductInfo
import com.loopers.domain.vo.Price
import com.loopers.support.enums.product.ProductStatusType

class ProductV1Dto {
    data class CreateRequest(
        val brandId: Long,
        val name: String,
        val price: Long,
        val description: String,
        val status: ProductStatusType,
    ) {
        fun toCriteria(username: String): ProductCriteria.Create {
            return ProductCriteria.Create(
                username,
                brandId,
                name,
                Price(price),
                description,
            )
        }
    }

    data class ProductResponse(
        val id: Long,
        val brandId: Long,
        val name: String,
        val description: String?,
        val price: Long,
        val status: ProductStatusType,
        val stockQuantity: Int,
    ) {
        companion object {
            fun from(product: ProductInfo.ProductResult): ProductResponse {
                return ProductResponse(
                    product.id,
                    product.brandId,
                    product.name,
                    product.description,
                    product.price,
                    product.status,
                    product.stockQuantity,
                )
            }
        }
    }

    data class ProductDetailResponse(
        val id: Long,
        val productName: String,
        val brandName: String,
        val productStatus: ProductStatusType,
        val productPrice: Long,
        val productLikeCount: Int,
    ) {
        companion object {
            fun from(productDetail: ProductInfo.ProductDetail): ProductDetailResponse {
                return ProductDetailResponse(
                    productDetail.id,
                    productDetail.productName,
                    productDetail.brandName,
                    productDetail.productStatus,
                    productDetail.productPrice,
                    productDetail.productLikeCount,
                )
            }
        }
    }
}
