package com.loopers.interfaces.api.productrank

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductRankV1ApiSpec {
    @Operation(
        summary = "상품 랭킹 목록 조회",
        description = "요청한 정보로 상품 랭킹 목록을 조회합니다.",
    )
    fun getProductRanks(
        @Schema(name = "상품 랭킹 검색 조건", description = "상품 랭킹을 검색하기 위한 조건")
        request: ProductRankV1Dto.Request,
        pageable: Pageable,
    ): ApiResponse<Page<ProductRankV1Dto.ProductRankListResponse>>
}
