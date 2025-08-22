package com.loopers.application.payment

import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.gateway.PaymentGateway
import com.loopers.domain.payment.gateway.PaymentGatewayResult
import org.springframework.stereotype.Component

@Component
class PaymentFacade(
    private val paymentService: PaymentService,
    private val paymentGateway: PaymentGateway,
) {

    fun findPaymentByOrderId(orderId: Long): PaymentInfo.Detail? {
        return paymentService.findPaymentByOrderId(orderId)
            ?.let { PaymentInfo.Detail.from(it) }
    }

    fun getPgResultByOrderId(userId: Long, orderKey: String): PaymentGatewayResult.ListResult {
        return paymentGateway.getPaymentByOrderId(userId, orderKey)
    }

    fun updatePayment(criteria: PaymentCriteria.Update) {
        return paymentService.updatePayment(criteria.toCommand())
    }
}
