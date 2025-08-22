package com.loopers.interfaces.api.payment

import com.loopers.application.order.OrderFacade
import com.loopers.support.enums.payment.TransactionStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentV1Controller(
    private val orderFacade: OrderFacade,
) {

    @PostMapping("callback")
    fun callback(@RequestBody request: PaymentV1Dto.Callback) {
        when (request.status) {
            TransactionStatus.SUCCESS -> orderFacade.handlePaymentCompleted(request.orderId, request.transactionKey)
            TransactionStatus.FAILED -> orderFacade.handlePaymentFailed(request.orderId, request.transactionKey)
            else -> throw IllegalArgumentException("Unsupported transaction status: ${request.status}")
        }
    }
}
