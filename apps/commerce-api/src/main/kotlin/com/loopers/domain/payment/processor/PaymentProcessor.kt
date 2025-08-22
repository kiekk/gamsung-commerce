package com.loopers.domain.payment.processor

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentEntity
import com.loopers.support.enums.payment.PaymentMethodType

interface PaymentProcessor {
    fun pay(command: PaymentCommand.Pay): PaymentEntity

    fun cancel(command: PaymentCommand.Cancel)

    fun supports(method: PaymentMethodType): Boolean
}
