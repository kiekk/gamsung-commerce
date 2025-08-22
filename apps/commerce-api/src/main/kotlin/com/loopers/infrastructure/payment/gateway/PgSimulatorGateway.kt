package com.loopers.infrastructure.payment.gateway

import com.loopers.domain.payment.gateway.PaymentGateway
import com.loopers.domain.payment.gateway.PaymentGatewayCommand
import com.loopers.domain.payment.gateway.PaymentGatewayResult
import com.loopers.support.enums.payment.PaymentStatusType
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class PgSimulatorGateway(
    private val pgSimulatorFeignClient: PgSimulatorFeignClient,
) : PaymentGateway {

    private val log = LoggerFactory.getLogger(PgSimulatorGateway::class.java)

    @Retry(name = "pg-request", fallbackMethod = "requestPaymentFallback")
    @CircuitBreaker(name = "pg-request")
    override fun requestPayment(userId: Long, command: PaymentGatewayCommand.Request): PaymentGatewayResult.Requested {
        return pgSimulatorFeignClient.createPayment(userId, command).data!!
    }

    override fun getPayment(userId: Long, transactionKey: String): PaymentGatewayResult.DetailResult {
        return pgSimulatorFeignClient.getPayment(userId, transactionKey).data!!
    }

    private fun requestPaymentFallback(
        userId: Long, command: PaymentGatewayCommand.Request, ex: Throwable,
    ): PaymentGatewayResult.Requested {
        log.error("PG createPayment failed for userId: $userId, command: $command", ex)
        return PaymentGatewayResult.Requested(
            null,
            PaymentStatusType.FAILED,
        )
    }
}
