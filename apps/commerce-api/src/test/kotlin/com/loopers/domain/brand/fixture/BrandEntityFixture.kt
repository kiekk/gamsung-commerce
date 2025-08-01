package com.loopers.domain.brand.fixture

import com.loopers.domain.brand.BrandEntity
import com.loopers.support.enums.brand.BrandStatusType

class BrandEntityFixture {
    private var name: String = "brandName"
    private var status: BrandStatusType = BrandStatusType.ACTIVE

    companion object {
        fun aBrand(): BrandEntityFixture = BrandEntityFixture()
    }

    fun name(name: String): BrandEntityFixture = apply { this.name = name }

    fun status(status: BrandStatusType): BrandEntityFixture = apply { this.status = status }

    fun build(): BrandEntity = BrandEntity(
        name,
        status,
    )
}
