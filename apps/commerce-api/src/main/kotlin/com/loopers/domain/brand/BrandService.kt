package com.loopers.domain.brand

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BrandService(
    private val brandRepository: BrandRepository,
) {

    @Transactional
    fun createBrand(brand: BrandEntity): BrandEntity {
        brandRepository.findByName(brand.name)?.let {
            throw CoreException(
                ErrorType.CONFLICT,
                "이미 존재하는 브랜드입니다: ${brand.name}",
            )
        }
        return brandRepository.save(brand)
    }

    @Transactional(readOnly = true)
    fun findBrandBy(brandId: Long): BrandEntity? {
        return brandRepository.findById(brandId)
    }
}
