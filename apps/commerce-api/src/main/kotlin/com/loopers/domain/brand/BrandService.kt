package com.loopers.domain.brand

import com.loopers.support.cache.CacheKey
import com.loopers.support.cache.CacheNames
import com.loopers.support.cache.CacheRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BrandService(
    private val brandRepository: BrandRepository,
    private val cacheRepository: CacheRepository,
) {

    private val log = LoggerFactory.getLogger(BrandService::class.java)

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
    fun findBrandBy(brandId: Long): BrandEntity? {
        val cache = cacheRepository.get(
            CacheKey(CacheNames.BRAND_DETAIL_V1, brandId.toString()),
            BrandEntity::class.java,
        )
        // 캐시가 존재
        cache?.let {
            log.info("[Cache Hit] Brand: $cache")
            return it
        }
        val brand = brandRepository.findById(brandId)
        // 캐시 저장
        brand?.let {
            log.info("[Cache Miss] Brand: $it")
            cacheRepository.set(CacheKey(CacheNames.BRAND_DETAIL_V1, brandId.toString()), it)
        }
        return brand
    }
}
