package com.loopers.interfaces.api.order

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.servlet.http.HttpServletRequest

interface OrderV1ApiSpec {
    @Operation(
        summary = "주문 조회",
        description = "요청한 정보로 주문을 조회합니다.",
    )
    fun getOrder(
        @Schema(name = "주문 ID", description = "조회할 주문의 ID")
        orderId: Long,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<OrderV1Dto.OrderResponse>

    @Operation(
        summary = "주문 생성 / 결제 처리",
        description = "요청한 정보로 주문을 생성 & 결제 처리합니다.",
    )
    fun placeOrder(
        @Schema(name = "주문 / 결제 정보", description = "생성할 주문의 정보")
        request: OrderV1Dto.CreateRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<Long>
}
