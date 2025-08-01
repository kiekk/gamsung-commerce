package com.loopers.domain.payment

import com.loopers.support.enums.payment.PaymentMethodType

class PaymentProcessorCommand {
    data class Process(
        val userId: String,
        val paymentId: Long,
        val paymentMethod: PaymentMethodType,
    )

    data class Cancel(
        val userId: String,
        val paymentId: Long,
        val paymentMethod: PaymentMethodType,
    )
}
