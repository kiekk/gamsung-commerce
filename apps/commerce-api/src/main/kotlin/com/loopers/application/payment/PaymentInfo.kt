package com.loopers.application.payment

import com.loopers.domain.payment.PaymentEntity
import com.loopers.support.enums.payment.PaymentCardType
import com.loopers.support.enums.payment.PaymentMethodType
import com.loopers.support.enums.payment.PaymentStatusType

class PaymentInfo {
    data class Detail(
        val id: Long,
        val orderId: Long,
        val userId: Long,
        val method: PaymentMethodType,
        val totalPrice: Long,
        var transactionKey: String?,
        var cardType: PaymentCardType?,
        var cardNo: String?,
        var reason: String?,
        var status: PaymentStatusType,
        val orderKey: String?,
    ) {

        fun isCardType(): Boolean {
            return method == PaymentMethodType.CARD
        }

        companion object {
            fun from(entity: PaymentEntity): Detail {
                return Detail(
                    entity.id,
                    entity.orderId,
                    entity.userId,
                    entity.method,
                    entity.totalPrice.value,
                    entity.transactionKey,
                    entity.cardType,
                    entity.cardNo,
                    entity.reason,
                    entity.status,
                    entity.orderKey,
                )
            }
        }
    }
}
