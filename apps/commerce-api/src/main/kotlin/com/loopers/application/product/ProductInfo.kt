package com.loopers.application.product

import com.loopers.domain.brand.BrandEntity
import com.loopers.domain.product.ProductEntity
import com.loopers.domain.productlike.ProductLikeCountEntity
import com.loopers.domain.stock.StockEntity
import com.loopers.domain.vo.Price

class ProductInfo {

    data class ProductResult(
        val id: Long,
        val brandId: Long,
        val name: String,
        val description: String?,
        val price: Price,
        val status: ProductEntity.ProductStatusType,
        val stockQuantity: Int,
    ) {
        companion object {
            fun from(product: ProductEntity, stock: StockEntity): ProductResult {
                return ProductResult(
                    product.id,
                    product.brandId,
                    product.name,
                    product.description,
                    product.price,
                    product.status,
                    stock.quantity,
                )
            }
        }
    }

    data class ProductDetail(
        val id: Long,
        val productName: String,
        val brandName: String,
        val productStatus: ProductEntity.ProductStatusType,
        val productPrice: Price,
        val productLikeCount: Int,
    ) {
        companion object {
            fun from(product: ProductEntity, brand: BrandEntity, productLikeCountEntity: ProductLikeCountEntity?): ProductDetail {
                return ProductDetail(
                    product.id,
                    product.name,
                    brand.name,
                    product.status,
                    product.price,
                    productLikeCountEntity?.productLikeCount ?: 0,
                )
            }
        }
    }

}
