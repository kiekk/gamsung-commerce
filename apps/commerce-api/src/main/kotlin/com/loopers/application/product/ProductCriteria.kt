package com.loopers.application.product

import com.loopers.domain.product.ProductCommand
import com.loopers.domain.vo.Price
import com.loopers.support.enums.product.ProductStatusType

class ProductCriteria {
    data class Create(
        val username: String,
        val brandId: Long,
        val name: String,
        val price: Price,
        val description: String?,
        val status: ProductStatusType,
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

        fun toCommand(): ProductCommand.Create {
            return ProductCommand.Create(
                brandId,
                name,
                price,
                description,
                status,
            )
        }

        companion object {
            private val PRODUCT_NAME_REGEX = "^[가-힣a-zA-Z0-9]{1,20}$".toRegex()
            private val PRODUCT_DESCRIPTION_REGEX = "^.{0,100}$".toRegex()
        }
    }
}
