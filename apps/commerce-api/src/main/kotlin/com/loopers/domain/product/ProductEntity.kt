package com.loopers.domain.product

import com.loopers.domain.vo.Price

class ProductEntity(
    val brandId: Long,
    val name: String,
    val description: String? = null,
    val price: Price,
    val status: ProductStatusType = ProductStatusType.ACTIVE,
) {

    enum class ProductStatusType {
        ACTIVE,
        INACTIVE,
        DELETED
    }

    init {
        !name.matches(PRODUCT_NAME_REGEX) && throw IllegalArgumentException("상품명은 한글, 영문, 숫자 20자 이내로 입력해야 합니다.")
        description?.let {
            !it.matches(PRODUCT_DESCRIPTION_REGEX) && throw IllegalArgumentException("상품 설명은 최대 100자 이내로 입력해야 합니다.")
        }
    }

    companion object {
        private val PRODUCT_NAME_REGEX = "^[가-힣a-zA-Z0-9]{1,20}$".toRegex()
        private val PRODUCT_DESCRIPTION_REGEX = "^.{0,100}$".toRegex()
    }
}
