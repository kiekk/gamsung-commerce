package com.loopers.interfaces.api.order

import com.loopers.application.order.OrderCriteria
import com.loopers.application.order.OrderFacade
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderV1Controller(
    private val orderFacade: OrderFacade,
) : OrderV1ApiSpec {

    @GetMapping("/{orderId}")
    override fun getOrder(
        @PathVariable("orderId") orderId: Long,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<OrderV1Dto.OrderResponse> {
        val username = httpServletRequest.getHeader("X-USER-ID")
            ?: throw CoreException(ErrorType.BAD_REQUEST, "X-USER-ID가 존재하지 않습니다.")
        val orderDetail = orderFacade.getOrder(OrderCriteria.Get(username, orderId))
        return ApiResponse.success(OrderV1Dto.OrderResponse.from(orderDetail))
    }

    @PostMapping("")
    override fun placeOrder(
        @RequestBody request: OrderV1Dto.CreateRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<Long> {
        val username = httpServletRequest.getHeader("X-USER-ID")
            ?: throw CoreException(ErrorType.BAD_REQUEST, "X-USER-ID가 존재하지 않습니다.")
        return orderFacade.placeOrder(request.toCriteria(username))
            .let { ApiResponse.success(it) }
    }
}
