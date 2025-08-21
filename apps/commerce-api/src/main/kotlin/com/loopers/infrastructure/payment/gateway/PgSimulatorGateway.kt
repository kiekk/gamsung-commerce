package com.loopers.infrastructure.payment.gateway

import com.loopers.domain.payment.gateway.PaymentGateway
import com.loopers.domain.payment.gateway.PaymentGatewayCommand
import com.loopers.domain.payment.gateway.PaymentGatewayResult
import org.springframework.stereotype.Component

@Component
class PgSimulatorGateway(
    private val pgSimulatorFeignClient: PgSimulatorFeignClient,
) : PaymentGateway {
    override fun requestPayment(userId: Long, command: PaymentGatewayCommand.Request): PaymentGatewayResult.Requested {
        return pgSimulatorFeignClient.createPayment(userId, command).data!!
    }

    override fun getPayment(userId: Long, transactionKey: String): PaymentGatewayResult.DetailResult {
        return pgSimulatorFeignClient.getPayment(userId, transactionKey).data!!
    }
}
