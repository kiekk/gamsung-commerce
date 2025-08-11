package com.loopers.domain.brand.fixture

import com.loopers.domain.brand.BrandEntity

class BrandEntityFixture {
    private var name: String = "brandName"

    companion object {
        fun aBrand(): BrandEntityFixture = BrandEntityFixture()
    }

    fun name(name: String): BrandEntityFixture = apply { this.name = name }

    fun build(): BrandEntity = BrandEntity(
        name,
    )
}
