package com.loopers.domain.payment

import com.loopers.domain.payment.processor.factory.PaymentProcessorFactory
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val paymentProcessorFactory: PaymentProcessorFactory,
) {
    fun findById(paymentId: Long): PaymentEntity? {
        return paymentRepository.findWithItemsById(paymentId)
    }

    fun pay(command: PaymentCommand.Pay): PaymentEntity {
        return paymentProcessorFactory.pay(command)
    }

    fun cancel(command: PaymentCommand.Cancel) {
        return paymentProcessorFactory.cancel(command)
    }
}
