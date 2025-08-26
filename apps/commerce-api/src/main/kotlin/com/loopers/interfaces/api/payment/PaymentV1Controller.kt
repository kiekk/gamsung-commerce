package com.loopers.interfaces.api.payment

import com.loopers.event.payload.payment.PaymentCompletedEvent
import com.loopers.event.payload.payment.PaymentFailedEvent
import com.loopers.event.publisher.EventPublisher
import com.loopers.support.enums.payment.TransactionStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentV1Controller(
    private val eventPublisher: EventPublisher,
) {

    @PostMapping("callback")
    fun callback(@RequestBody request: PaymentV1Dto.Callback) {
        when (request.status) {
            TransactionStatus.SUCCESS -> eventPublisher.publish(
                PaymentCompletedEvent(
                    request.orderId,
                    request.transactionKey,
                ),
            )

            TransactionStatus.FAILED -> eventPublisher.publish(
                PaymentFailedEvent(
                    request.orderId,
                    request.transactionKey,
                ),
            )

            else -> throw IllegalArgumentException("Unsupported transaction status: ${request.status}")
        }
    }
}
