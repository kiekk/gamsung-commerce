package com.loopers.domain.payment.processor.factory

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentEntity
import com.loopers.domain.payment.processor.PaymentProcessor
import org.springframework.stereotype.Component

@Component
class PaymentProcessorFactory(
    private val processors: List<PaymentProcessor>,
) {
    fun pay(command: PaymentCommand.Pay): PaymentEntity {
        return processors.find { it.supports(command.paymentMethod) }?.pay(command)
            ?: throw IllegalArgumentException("지원하지 않는 결제 방법입니다: ${command.paymentMethod}")
    }

    fun cancel(command: PaymentCommand.Cancel) {
        return processors.find { it.supports(command.paymentMethod) }?.cancel(command)
            ?: throw IllegalArgumentException("지원하지 않는 결제 방법입니다: ${command.paymentMethod}")
    }
}
