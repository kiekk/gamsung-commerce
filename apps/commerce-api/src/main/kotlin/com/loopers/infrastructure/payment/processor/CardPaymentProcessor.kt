package com.loopers.infrastructure.payment.processor

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentEntity
import com.loopers.domain.payment.PaymentRepository
import com.loopers.domain.payment.gateway.PaymentGateway
import com.loopers.domain.payment.gateway.PaymentGatewayCommand
import com.loopers.domain.payment.processor.PaymentProcessor
import com.loopers.support.enums.payment.PaymentMethodType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CardPaymentProcessor(
    private val paymentGateway: PaymentGateway,
    private val paymentRepository: PaymentRepository,
) : PaymentProcessor {

    @Value("\${pg-simulator.callback}")
    private lateinit var callback: String

    override fun pay(command: PaymentCommand.Pay): PaymentEntity {
        val requestedPayment = paymentGateway.requestPayment(
            command.userId,
            PaymentGatewayCommand.Request(
                command.orderKey,
                command.cardType,
                command.cardNo,
                command.totalPrice.value,
                callback,
            ),
        )
        return paymentRepository.save(command.toPaymentEntity(requestedPayment.transactionKey, requestedPayment.status))
    }

    override fun cancel(command: PaymentCommand.Cancel) {
        // TODO: pg 결제 취소 요청
    }

    override fun supports(method: PaymentMethodType): Boolean {
        return method == PaymentMethodType.CARD
    }
}
