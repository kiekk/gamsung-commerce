package com.loopers.domain.brand.query

import com.loopers.domain.brand.BrandEntity

class BrandSearchCondition(
    val name: String? = null,
    val status: BrandEntity.BrandStatusType? = null,
) {

    init {
        name?.let {
            require(it.matches(BRAND_NAME_REGEX)) { "브랜드명은 한글, 영문, 숫자 20자 이내로 입력해야 합니다." }
        }
    }

    companion object {
        private val BRAND_NAME_REGEX = "^[가-힣a-zA-Z0-9]{1,20}$".toRegex()
    }
}
