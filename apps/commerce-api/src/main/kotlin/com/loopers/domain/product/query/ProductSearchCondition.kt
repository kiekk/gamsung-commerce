package com.loopers.domain.product.query

import java.math.BigDecimal

class ProductSearchCondition(
    var name: String? = null,
    var minPrice: BigDecimal? = null,
    var maxPrice: BigDecimal? = null,
    var brandId: Long? = null,
) {

    init {
        name?.let {
            !it.matches(PRODUCT_NAME_REGEX) && throw IllegalArgumentException("상품명은 한글, 영문, 숫자 20자 이내로 입력해야 합니다.")
        }
        minPrice?.let {
            require(it >= BigDecimal.ZERO) { "최소 가격은 0 이상이어야 합니다." }
        }
        maxPrice?.let {
            require(it >= BigDecimal.ZERO) { "최대 가격은 0 이상이어야 합니다." }
        }
    }

    fun isEmpty(): Boolean {
        return name.isNullOrBlank() && minPrice == null && maxPrice == null && brandId == null
    }

    companion object {
        private val PRODUCT_NAME_REGEX = "^[가-힣a-zA-Z0-9]{1,20}$".toRegex()
    }
}
