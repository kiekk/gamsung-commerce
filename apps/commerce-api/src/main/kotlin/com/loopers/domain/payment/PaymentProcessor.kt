package com.loopers.domain.payment

import com.loopers.support.enums.payment.PaymentMethodType

interface PaymentProcessor {
    fun process(command: PaymentProcessorCommand.Process)

    fun cancel(command: PaymentProcessorCommand.Cancel)

    fun supports(method: PaymentMethodType): Boolean
}
