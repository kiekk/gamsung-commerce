package com.loopers.domain.payment

import com.loopers.domain.vo.Price
import com.loopers.support.enums.payment.PaymentMethodType

class PaymentCommand {
    data class Pay(
        val orderId: Long,
        val userId: Long,
        val paymentMethod: PaymentMethodType,
        val totalPrice: Price,
    ) {
        init {
            require(orderId > 0) { "주문 아이디는 0보다 커야 합니다." }
        }

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
