package com.loopers.domain.brand

class BrandCommand {
    data class Create(
        val name: String,
    ) {
        init {
            require(name.matches(BRAND_NAME_REGEX)) { "브랜드명은 한글, 영문, 숫자 20자 이내로 입력해야 합니다." }
        }

        fun toEntity(): BrandEntity {
            return BrandEntity(
                name,
            )
        }

        companion object {
            private val BRAND_NAME_REGEX = "^[가-힣a-zA-Z0-9]{1,20}$".toRegex()
        }
    }
}
