package com.loopers.domain.payment

import com.loopers.domain.BaseEntity
import com.loopers.domain.payment.vo.PaymentItems
import com.loopers.domain.vo.Price

class PaymentEntity(
    val orderId: Long,
    val method: PaymentMethodType,
    paymentItems: PaymentItems,
) : BaseEntity() {
    var status: PaymentStatusType
    val totalAmount: Price = paymentItems.totalAmount()

    init {
        status = PaymentStatusType.PENDING
    }

    enum class PaymentStatusType {
        PENDING,
        COMPLETED,
        FAILED
    }

    enum class PaymentMethodType {
        POINT,
    }
}
