package com.loopers.interfaces.api.productlike

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest

@Tag(name = "ProductLike V1 API", description = "상품 좋아요 API 입니다.")
interface ProductLikeV1ApiSpec {
    @Operation(
        summary = "상품 좋아요 등록",
        description = "요청한 정보로 상품 좋아요를 등록합니다.",
    )
    fun like(
        @Schema(name = "상품 ID", description = "좋아요를 등록할 상품의 ID")
        productId: Long,
        httpServletRequest: HttpServletRequest,
    )

    @Operation(
        summary = "상품 좋아요 취소",
        description = "요청한 정보로 상품 좋아요를 취소합니다.",
    )
    fun unlike(
        @Schema(name = "상품 ID", description = "좋아요를 취소할 상품의 ID")
        productId: Long,
        httpServletRequest: HttpServletRequest,
    )
}
