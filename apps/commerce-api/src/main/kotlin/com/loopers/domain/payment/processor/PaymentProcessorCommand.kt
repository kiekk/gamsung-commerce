package com.loopers.domain.payment.processor

import com.loopers.domain.payment.PaymentEntity
import com.loopers.domain.vo.Price
import com.loopers.support.enums.payment.PaymentMethodType

class PaymentProcessorCommand {
    data class Pay(
        val orderId: Long,
        val userId: Long,
        val paymentMethod: PaymentMethodType,
        val totalPrice: Price,
    ) {
        fun toPaymentEntity(): PaymentEntity {
            return PaymentEntity(
                orderId,
                userId,
                paymentMethod,
                totalPrice,
            )
        }
    }

    data class Cancel(
        val userId: Long,
        val paymentId: Long,
        val paymentMethod: PaymentMethodType,
    )
}
