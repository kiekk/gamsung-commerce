package com.loopers.interfaces.api.brand

import com.loopers.application.brand.BrandFacade
import com.loopers.domain.brand.query.BrandListViewModel
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/brands")
class BrandV1Controller(
    private val brandFacade: BrandFacade,
) : BrandV1ApiSpec {

    @PostMapping("")
    override fun createBrand(
        @RequestBody request: BrandV1Dto.CreateRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<BrandV1Dto.BrandResponse> {
        val username = httpServletRequest.getHeader("X-USER-ID")
            ?: throw CoreException(ErrorType.BAD_REQUEST, "X-USER-ID가 존재하지 않습니다.")
        return brandFacade.createBrand(request.toCriteria(username))
            .let { BrandV1Dto.BrandResponse.from(it) }
            .let { ApiResponse.success(it) }
    }

    @GetMapping("{brandId}")
    override fun getBrand(@PathVariable("brandId") brandId: Long): ApiResponse<BrandV1Dto.BrandResponse> {
        return brandFacade.getBrand(brandId)
            .let { BrandV1Dto.BrandResponse.from(it) }
            .let { ApiResponse.success(it) }
    }

    @GetMapping("")
    override fun getBrandPage(
        request: BrandV1Dto.QueryRequest,
        pageable: Pageable,
    ): ApiResponse<Page<BrandListViewModel>> {
        return brandFacade.searchBrands(request.toCriteria(), pageable)
            .let { ApiResponse.success(it) }
    }
}
