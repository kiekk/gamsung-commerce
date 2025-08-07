package com.loopers.application.brand

import com.loopers.domain.brand.BrandCommand
import com.loopers.domain.brand.query.BrandSearchCondition
import com.loopers.support.enums.brand.BrandStatusType

class BrandCriteria {
    data class Create(
        val username: String,
        val name: String,
        val status: BrandStatusType,
    ) {
        init {
            require(name.matches(BRAND_NAME_REGEX)) { "브랜드명은 한글, 영문, 숫자 20자 이내로 입력해야 합니다." }
        }

        fun toCommand(): BrandCommand.Create {
            return BrandCommand.Create(
                name,
                status,
            )
        }

        companion object {
            private val BRAND_NAME_REGEX = "^[가-힣a-zA-Z0-9]{1,20}$".toRegex()
        }
    }

    data class Query(
        val name: String? = null,
        val status: BrandStatusType? = null,
    ) {
        fun toCondition(): BrandSearchCondition {
            return BrandSearchCondition(
                name,
                status,
            )
        }
    }
}
