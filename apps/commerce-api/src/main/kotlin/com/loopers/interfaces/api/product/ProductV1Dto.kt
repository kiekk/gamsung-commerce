package com.loopers.interfaces.api.product

import com.loopers.application.product.ProductCriteria
import com.loopers.application.product.ProductInfo
import com.loopers.domain.product.query.ProductSearchCondition
import com.loopers.domain.vo.Price
import com.loopers.support.enums.product.ProductStatusType
import java.math.BigDecimal

class ProductV1Dto {

    data class SearchRequest(
        var name: String? = null,
        var minPrice: BigDecimal? = null,
        var maxPrice: BigDecimal? = null,
        var brandId: Long? = null,
    ) {
        fun toCriteria(): ProductSearchCondition {
            return ProductSearchCondition(
                name,
                minPrice,
                maxPrice,
                brandId,
            )
        }
    }

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

    data class ProductResultResponse(
        val id: Long,
        val brandId: Long,
        val name: String,
        val description: String?,
        val price: Long,
        val status: ProductStatusType,
        val stockQuantity: Int,
    ) {
        companion object {
            fun from(product: ProductInfo.ProductResult): ProductResultResponse {
                return ProductResultResponse(
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

    data class ProductListResponse(
        val id: Long,
        val productName: String,
        val productPrice: Long,
        val productStatus: ProductStatusType,
        val brandName: String,
        val productLikeCount: Int,
    ) {
        companion object {
            fun from(product: ProductInfo.ProductList): ProductListResponse {
                return ProductListResponse(
                    product.id,
                    product.productName,
                    product.productPrice,
                    product.productStatus,
                    product.brandName,
                    product.productLikeCount,
                )
            }
        }
    }
}
