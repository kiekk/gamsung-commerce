package com.loopers.interfaces.api.brand

import com.loopers.domain.brand.query.BrandListViewModel
import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BrandV1ApiSpec {
    @Operation(
        summary = "브랜드 생성",
        description = "요청한 정보로 브랜드를 생성합니다.",
    )
    fun createBrand(
        @Schema(name = "브랜드 생성 정보", description = "생성할 브랜드 정보")
        request: BrandV1Dto.CreateRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<BrandV1Dto.BrandResponse>

    @Operation(
        summary = "브랜드 조회",
        description = "요청한 정보로 브랜드 정보를 조회합니다.",
    )
    fun getBrand(
        @Schema(name = "브랜드 ID", description = "조회할 브랜드의 ID")
        brandId: Long,
    ): ApiResponse<BrandV1Dto.BrandResponse>

    @Operation(
        summary = "브랜드 목록 조회",
        description = "브랜드 목록을 조회합니다.",
    )
    fun getBrandPage(
        @Schema(name = "브랜드 조회 요청", description = "브랜드 조회에 필요한 필터링 정보")
        request: BrandV1Dto.QueryRequest,
        @Schema(name = "페이지 정보", description = "페이지네이션을 위한 정보")
        pageable: Pageable,
    ): ApiResponse<Page<BrandListViewModel>>
}
