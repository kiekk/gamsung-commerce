package com.loopers.interfaces.api.product

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductV1ApiSpec {
    @Operation(
        summary = "상품 생성",
        description = "요청한 정보로 상품을 생성합니다.",
    )
    fun createProduct(
        @Schema(name = "상품 생성 정보", description = "생성할 상품 정보")
        request: ProductV1Dto.CreateRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<ProductV1Dto.ProductResultResponse>

    @Operation(
        summary = "상품 조회",
        description = "요청한 정보로 상품 정보를 조회합니다.",
    )
    fun getProduct(
        @Schema(name = "상품 ID", description = "조회할 상품의 ID")
        productId: Long,
    ): ApiResponse<ProductV1Dto.ProductDetailResponse>

    @Operation(
        summary = "상품 목록 조회",
        description = "요청한 정보로 상품 목록을 조회합니다.",
    )
    fun getProducts(
        @Schema(name = "상품 검색 조건", description = "상품을 검색하기 위한 조건")
        request: ProductV1Dto.SearchRequest,
        @Schema(name = "페이지 정보", description = "상품 목록을 조회하기 위한 페이지 정보")
        pageable: Pageable,
    ): ApiResponse<Page<ProductV1Dto.ProductListResponse>>

    @Operation(
        summary = "상품 목록 조회 (Count Query)",
        description = "요청한 정보로 상품 목록을 조회합니다.",
    )
    fun getProductsByCountQuery(
        @Schema(name = "상품 검색 조건", description = "상품을 검색하기 위한 조건")
        request: ProductV1Dto.SearchRequest,
        @Schema(name = "페이지 정보", description = "상품 목록을 조회하기 위한 페이지 정보")
        pageable: Pageable,
    ): ApiResponse<Page<ProductV1Dto.ProductListResponse>>
}
