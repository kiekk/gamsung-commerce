package com.loopers.interfaces.api.product

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.servlet.http.HttpServletRequest

interface ProductV1ApiSpec {
    @Operation(
        summary = "상품 생성",
        description = "요청한 정보로 상품을 생성합니다.",
    )
    fun createProduct(
        @Schema(name = "상품 생성 정보", description = "생성할 상품 정보")
        request: ProductV1Dto.CreateRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<ProductV1Dto.ProductResponse>

    @Operation(
        summary = "상품 조회",
        description = "요청한 정보로 상품 정보를 조회합니다.",
    )
    fun getProduct(
        @Schema(name = "상품 ID", description = "조회할 상품의 ID")
        productId: Long,
    ): ApiResponse<ProductV1Dto.ProductDetailResponse>

}
