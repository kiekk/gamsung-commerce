package com.loopers.domain.payment

import com.loopers.domain.vo.Price
import com.loopers.support.enums.payment.PaymentCardType
import com.loopers.support.enums.payment.PaymentMethodType
import com.loopers.support.enums.payment.PaymentStatusType

class PaymentCommand {
    data class Pay(
        val orderId: Long,
        val userId: Long,
        val paymentMethod: PaymentMethodType,
        val totalPrice: Price,
        var cardType: PaymentCardType? = null,
        var cardNo: String? = null,
        val orderKey: String? = null,
    ) {
        init {
            require(orderId > 0) { "주문 아이디는 0보다 커야 합니다." }
        }

        fun toPaymentEntity(transactionKey: String): PaymentEntity {
            return PaymentEntity(
                orderId,
                userId,
                paymentMethod,
                totalPrice,
                transactionKey,
                orderKey = orderKey,
            )
        }

        fun toPaymentEntity(transactionKey: String?, paymentStatusType: PaymentStatusType): PaymentEntity {
            return PaymentEntity(
                orderId,
                userId,
                paymentMethod,
                totalPrice,
                transactionKey,
                cardType,
                cardNo,
                status = paymentStatusType,
                orderKey = orderKey,
            )
        }
    }

    data class Cancel(
        val userId: Long,
        val paymentId: Long,
        val paymentMethod: PaymentMethodType,
    )
}
