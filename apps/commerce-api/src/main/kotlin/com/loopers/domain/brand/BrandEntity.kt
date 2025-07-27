package com.loopers.domain.brand

import com.loopers.domain.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "brand")
class BrandEntity(
    @Column(unique = true)
    val name: String,
    @Enumerated(EnumType.STRING)
    val status: BrandStatusType,
) : BaseEntity() {

    enum class BrandStatusType {
        ACTIVE,
        INACTIVE,
        DELETED
    }

    init {
        !name.matches(BRAND_NAME_REGEX) && throw IllegalArgumentException("브랜드명은 한글, 영문, 숫자 20자 이내로 입력해야 합니다.")

    }

    companion object {
        private val BRAND_NAME_REGEX = "^[가-힣a-zA-Z0-9]{1,20}$".toRegex()
    }
}
