package com.loopers.domain.payment.factory

import com.loopers.domain.payment.PaymentProcessor
import com.loopers.domain.payment.PaymentProcessorCommand
import org.springframework.stereotype.Component

@Component
class PaymentProcessorFactory(
    private val processors: List<PaymentProcessor>,
) {
    fun process(command: PaymentProcessorCommand.Process) {
        return processors.find { it.supports(command.paymentMethod) }?.process(command)
            ?: throw IllegalArgumentException("지원하지 않는 결제 방법입니다: ${command.paymentMethod}")
    }
}
