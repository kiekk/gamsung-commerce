package com.loopers.domain.brand

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification

interface BrandRepository {
    fun save(brand: BrandEntity): BrandEntity

    fun findById(brandId: Long): BrandEntity?

    fun findByName(name: String): BrandEntity?

    fun findAll(spec: Specification<BrandEntity>, pageRequest: PageRequest): Page<BrandEntity>
}
