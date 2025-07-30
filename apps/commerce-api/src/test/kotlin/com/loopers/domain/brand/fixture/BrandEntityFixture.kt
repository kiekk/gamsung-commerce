package com.loopers.domain.brand.fixture

import com.loopers.domain.brand.BrandEntity

class BrandEntityFixture {
    private var name: String = "brandName"
    private var status: BrandEntity.BrandStatusType = BrandEntity.BrandStatusType.ACTIVE

    companion object {
        fun aBrand(): BrandEntityFixture = BrandEntityFixture()
    }

    fun name(name: String): BrandEntityFixture = apply { this.name = name }

    fun status(status: BrandEntity.BrandStatusType): BrandEntityFixture = apply { this.status = status }

    fun build(): BrandEntity = BrandEntity(
        name,
        status,
    )
}
