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

    fun findPaymentByOrderId(orderId: Long): PaymentEntity? {
        return paymentRepository.findByOrderId(orderId)
    }

    fun updatePayment(command: PaymentCommand.Update) {
        val payment = paymentRepository.findWithItemsById(command.id)
            ?: throw IllegalArgumentException("결제 정보가 존재하지 않습니다. id: ${command.id}")

        payment.updateStatus(command.status)
        payment.updateTransactionKey(command.transactionKey)
    }

    fun failPayment(transactionKey: String) {
        val payment = paymentRepository.findWithItemsByTransactionKey(transactionKey)
            ?: throw IllegalArgumentException("결제 정보를 찾을 수 없습니다. transactionKey: $transactionKey")

        // 결제 실패 상태 변경
        payment.fail()
    }
}
