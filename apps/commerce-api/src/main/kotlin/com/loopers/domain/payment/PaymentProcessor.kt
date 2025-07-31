package com.loopers.domain.payment

interface PaymentProcessor {
    fun process(command: PaymentProcessorCommand.Process)

    fun supports(method: PaymentEntity.PaymentMethodType): Boolean
}
