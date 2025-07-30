package com.loopers.domain.payment

import com.loopers.domain.BaseEntity
import com.loopers.domain.vo.Price

class PaymentItemEntity(
    val paymentId: Long,
    val orderItemId: Long,
    val amount: Price,
) : BaseEntity() {
    var status: PaymentItemStatusType

    enum class PaymentItemStatusType {
        PENDING,
        COMPLETED,
        FAILED
    }

    init {
        status = PaymentItemStatusType.PENDING
    }

    fun complete() {
        status = PaymentItemStatusType.COMPLETED
    }

    fun fail() {
        status = PaymentItemStatusType.FAILED
    }
}
