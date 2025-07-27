package com.loopers.domain.brand

interface BrandRepository {
    fun save(brand: BrandEntity): BrandEntity

    fun findById(brandId: Long): BrandEntity?

    fun findByName(name: String): BrandEntity?
}
