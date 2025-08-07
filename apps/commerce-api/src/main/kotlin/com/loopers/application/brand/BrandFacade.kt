package com.loopers.application.brand

import com.loopers.domain.brand.BrandService
import com.loopers.domain.brand.query.BrandListViewModel
import com.loopers.domain.brand.query.BrandQueryService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class BrandFacade(
    private val brandService: BrandService,
    private val brandQueryService: BrandQueryService,
) {

    fun createBrand(criteria: BrandCriteria.Create): BrandInfo.BrandResponse {
        return brandService.createBrand(criteria.toCommand())
            .let { BrandInfo.BrandResponse.from(it) }
    }

    fun findBrandBy(brandId: Long): BrandInfo.BrandResponse {
        val brand = brandService.findBrandBy(brandId) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "브랜드를 찾을 수 없습니다. id: $brandId",
        )
        return brand.let { BrandInfo.BrandResponse.from(it) }
    }

    fun searchBrands(criteria: BrandCriteria.Query, pageable: Pageable): Page<BrandListViewModel> {
        return brandQueryService.searchBrands(criteria.toCondition(), pageable)
    }
}
