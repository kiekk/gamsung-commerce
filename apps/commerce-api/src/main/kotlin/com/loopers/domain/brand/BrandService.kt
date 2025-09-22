package com.loopers.domain.brand

import com.loopers.support.cache.CacheNames
import com.loopers.support.cache.optimized.OptimizedCacheable
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BrandService(
    private val brandRepository: BrandRepository,
) {

    @Transactional
    fun createBrand(command: BrandCommand.Create): BrandEntity {
        brandRepository.findByName(command.name)?.let {
            throw CoreException(
                ErrorType.CONFLICT,
                "이미 존재하는 브랜드입니다: ${command.name}",
            )
        }
        return brandRepository.save(command.toEntity())
    }

    @Transactional(readOnly = true)
    @OptimizedCacheable(type = CacheNames.BRAND_DETAIL_V1, ttlSeconds = 10)
    fun findBrandBy(brandId: Long): BrandEntity? {
        return brandRepository.findById(brandId)
    }
}
