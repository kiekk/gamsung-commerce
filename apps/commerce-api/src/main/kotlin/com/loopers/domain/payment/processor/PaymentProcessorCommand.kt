package com.loopers.domain.payment.processor

import com.loopers.support.enums.payment.PaymentMethodType

class PaymentProcessorCommand {
    data class Process(
        val userId: Long,
        val paymentId: Long,
        val paymentMethod: PaymentMethodType,
    )

    data class Cancel(
        val userId: Long,
        val paymentId: Long,
        val paymentMethod: PaymentMethodType,
    )
}
