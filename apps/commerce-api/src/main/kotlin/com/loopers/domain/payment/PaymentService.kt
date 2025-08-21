package com.loopers.domain.payment

import com.loopers.domain.payment.processor.factory.PaymentProcessorFactory
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val paymentProcessorFactory: PaymentProcessorFactory,
) {
    fun findByTransactionKey(transactionKey: String): PaymentEntity? {
        return paymentRepository.findWithItemsByTransactionKey(transactionKey)
    }

    fun pay(command: PaymentCommand.Pay): PaymentEntity {
        return paymentProcessorFactory.pay(command)
    }

    fun cancel(command: PaymentCommand.Cancel) {
        return paymentProcessorFactory.cancel(command)
    }
}
