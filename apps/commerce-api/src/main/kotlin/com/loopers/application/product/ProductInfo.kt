package com.loopers.application.product

import com.loopers.domain.brand.BrandEntity
import com.loopers.domain.product.ProductEntity
import com.loopers.domain.product.query.ProductListViewModel
import com.loopers.domain.productlike.ProductLikeCountEntity
import com.loopers.domain.stock.StockEntity
import com.loopers.domain.vo.Price
import com.loopers.support.enums.product.ProductStatusType
import java.time.ZonedDateTime

class ProductInfo {

    data class ProductResult(
        val id: Long,
        val brandId: Long,
        val name: String,
        val description: String?,
        val price: Price,
        val status: ProductStatusType,
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
        val productStatus: ProductStatusType,
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

    data class ProductList(
        val id: Long,
        val productName: String,
        val productPrice: Long,
        val productStatus: ProductStatusType,
        val brandName: String,
        val productLikeCount: Int,
        val createdAt: ZonedDateTime,
    ) {
        companion object {
            fun from(productListViewModel: ProductListViewModel): ProductList {
                return ProductList(
                    productListViewModel.id,
                    productListViewModel.name,
                    productListViewModel.price,
                    productListViewModel.productStatus,
                    productListViewModel.brandName,
                    productListViewModel.productLikeCount,
                    productListViewModel.createdAt,
                )
            }
        }
    }
}
