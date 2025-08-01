package com.loopers.domain.brand

import com.loopers.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "brand")
class BrandEntity(
    @Column(unique = true)
    val name: String,
    @Enumerated(EnumType.STRING)
    var status: BrandStatusType,
) : BaseEntity() {

    enum class BrandStatusType {
        ACTIVE,
        INACTIVE,
        CLOSED,
    }

    init {
        !name.matches(BRAND_NAME_REGEX) && throw IllegalArgumentException("브랜드명은 한글, 영문, 숫자 20자 이내로 입력해야 합니다.")
    }

    fun active() {
        this.status = BrandStatusType.ACTIVE
    }

    fun inactive() {
        this.status = BrandStatusType.INACTIVE
    }

    fun close() {
        this.status = BrandStatusType.CLOSED
    }

    companion object {
        private val BRAND_NAME_REGEX = "^[가-힣a-zA-Z0-9]{1,20}$".toRegex()
    }
}
