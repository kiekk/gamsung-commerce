package com.loopers.infrastructure.payment.gateway

import com.loopers.domain.payment.gateway.PaymentGatewayCommand
import com.loopers.domain.payment.gateway.PaymentGatewayResult
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.config.feign.FeignConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "pg-simulator",
    url = "\${pg-simulator.url}",
    configuration = [FeignConfig::class],
)
interface PgSimulatorFeignClient {
    @PostMapping("/api/v1/payments", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createPayment(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: PaymentGatewayCommand.Request,
    ): ApiResponse<PaymentGatewayResult.Requested>

    @GetMapping("/api/v1/payments/{paymentId}")
    fun getPayment(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable paymentId: String,
    ): ApiResponse<PaymentGatewayResult.DetailResult>

    @GetMapping("/api/v1/payments")
    fun getPaymentsByOrderId(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam orderId: String,
    ): List<PaymentGatewayResult.ListResult>
}
