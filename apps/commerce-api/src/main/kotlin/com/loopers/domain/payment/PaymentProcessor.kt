package com.loopers.domain.payment

interface PaymentProcessor {
    fun process(command: PaymentProcessorCommand.Process)

    fun cancel(command: PaymentProcessorCommand.Cancel)

    fun supports(method: PaymentEntity.PaymentMethodType): Boolean
}
