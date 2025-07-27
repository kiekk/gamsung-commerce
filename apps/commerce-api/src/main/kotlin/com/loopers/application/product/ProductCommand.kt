package com.loopers.application.product

import com.loopers.domain.product.ProductEntity
import com.loopers.domain.stock.StockEntity
import com.loopers.domain.vo.Price

class ProductCommand {
    data class Create(
        val brandId: Long,
        val name: String,
        val price: Price,
        val description: String?,
        val status: ProductEntity.ProductStatusType,
        val quantity: Int? = 0,
    ) {

        init {
            require(brandId > 0) { "브랜드 ID는 1 이상이어야 합니다." }
            !name.matches(PRODUCT_NAME_REGEX) && throw IllegalArgumentException("상품명은 한글, 영문, 숫자 20자 이내로 입력해야 합니다.")
            description?.let {
                !it.matches(PRODUCT_DESCRIPTION_REGEX) && throw IllegalArgumentException("상품 설명은 최대 100자 이내로 입력해야 합니다.")
            }
            require(price.value > 0) { "상품 가격은 0 이상이어야 합니다." }
            quantity?.let {
                require(it >= 0) { "재고 수량은 0 이상이어야 합니다." }
            }
        }

        fun toProductEntity(): ProductEntity {
            return ProductEntity(
                brandId,
                name,
                description,
                price,
                status,
            )
        }

        fun toStockEntity(productId: Long): StockEntity {
            return StockEntity(
                productId,
                quantity ?: 0,
            )
        }

        companion object {
            private val PRODUCT_NAME_REGEX = "^[가-힣a-zA-Z0-9]{1,20}$".toRegex()
            private val PRODUCT_DESCRIPTION_REGEX = "^.{0,100}$".toRegex()
        }
    }

    data class ProductInfo(
        val id: Long,
        val brandId: Long,
        val name: String,
        val description: String?,
        val price: Price,
        val status: ProductEntity.ProductStatusType,
        val stockQuantity: Int,
    ) {

        companion object {
            fun from(product: ProductEntity, stock: StockEntity): ProductInfo {
                return ProductInfo(
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
}
