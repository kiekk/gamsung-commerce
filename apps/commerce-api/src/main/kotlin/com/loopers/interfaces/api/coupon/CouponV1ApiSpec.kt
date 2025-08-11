package com.loopers.interfaces.api.coupon

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest

@Tag(name = "Coupon V1 API", description = "쿠폰 API 입니다.")
interface CouponV1ApiSpec {
    @Operation(
        summary = "쿠폰 생성",
        description = "요청한 정보로 쿠폰을 생성합니다.",
    )
    fun createCoupon(
        @Schema(name = "쿠폰 생성 정보", description = "생성할 쿠폰 정보")
        request: CouponV1Dto.CreateRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<CouponV1Dto.CouponDetail>

    @Operation(
        summary = "쿠폰 발급",
        description = "요청한 정보로 쿠폰을 발급합니다.",
    )
    fun issueCoupon(
        @Schema(name = "쿠폰 ID", description = "발급할 쿠폰의 ID")
        couponId: Long,
        @Schema(name = "쿠폰 발급 사용자 정보", description = "쿠폰을 발급받을 사용자 정보")
        request: CouponV1Dto.IssueRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<CouponV1Dto.IssuedCouponDetail>
}
