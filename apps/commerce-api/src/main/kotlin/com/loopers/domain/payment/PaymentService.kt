package com.loopers.domain.payment

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
) {
    @Transactional
    fun createPayment(command: PaymentCommand.Create): PaymentEntity {
        val payment = command.toPaymentEntity()
        return paymentRepository.save(payment)
    }
}
