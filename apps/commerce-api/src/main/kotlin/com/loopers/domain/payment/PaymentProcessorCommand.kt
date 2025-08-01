package com.loopers.domain.payment

class PaymentProcessorCommand {
    data class Process(
        val userId: String,
        val paymentId: Long,
        val paymentMethod: PaymentEntity.PaymentMethodType,
    )

    data class Cancel(
        val userId: String,
        val paymentId: Long,
        val paymentMethod: PaymentEntity.PaymentMethodType,
    )
}
