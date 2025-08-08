package com.loopers.domain.payment.processor

import com.loopers.support.enums.payment.PaymentMethodType

interface PaymentProcessor {
    fun pay(command: PaymentProcessorCommand.Pay)

    fun cancel(command: PaymentProcessorCommand.Cancel)

    fun supports(method: PaymentMethodType): Boolean
}
