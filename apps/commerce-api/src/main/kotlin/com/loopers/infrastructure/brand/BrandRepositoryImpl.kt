package com.loopers.infrastructure.brand

import com.loopers.domain.brand.BrandEntity
import com.loopers.domain.brand.BrandRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Repository

@Repository
class BrandRepositoryImpl(
    private val brandJpaRepository: BrandJpaRepository,
) : BrandRepository {

    override fun save(brand: BrandEntity): BrandEntity {
        return brandJpaRepository.save(brand)
    }

    override fun findById(brandId: Long): BrandEntity? {
        return brandJpaRepository.findById(brandId).orElse(null)
    }

    override fun findByName(name: String): BrandEntity? {
        return brandJpaRepository.findByName(name)
    }

    override fun findAll(
        spec: Specification<BrandEntity>,
        pageRequest: PageRequest,
    ): Page<BrandEntity> {
        return brandJpaRepository.findAll(spec, pageRequest)
    }
}
