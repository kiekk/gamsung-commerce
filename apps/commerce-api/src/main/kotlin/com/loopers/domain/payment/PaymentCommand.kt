package com.loopers.domain.payment

import com.loopers.domain.vo.Price

class PaymentCommand {
    data class Create(
        val orderId: Long,
        val method: PaymentEntity.PaymentMethodType,
        val paymentItems: List<PaymentItemCommand>,
    ) {
        init {
            require(orderId > 0) { "주문 아이디는 0보다 커야 합니다." }
            require(paymentItems.isNotEmpty()) { "결제 항목은 최소 하나 이상이어야 합니다." }
        }

        data class PaymentItemCommand(
            val orderItemId: Long,
            val amount: Price,
        )

        fun toPaymentEntity(): PaymentEntity {
            return PaymentEntity(
                orderId,
                method,
            )
        }

        fun toPaymentItemEntities(payment: PaymentEntity): List<PaymentItemEntity> {
            return paymentItems.map { command ->
                PaymentItemEntity(
                    payment,
                    command.orderItemId,
                    command.amount,
                )
            }
        }

    }
}
