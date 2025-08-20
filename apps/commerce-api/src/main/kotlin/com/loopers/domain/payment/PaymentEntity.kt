package com.loopers.domain.payment

import com.loopers.domain.BaseEntity
import com.loopers.domain.vo.Price
import com.loopers.support.enums.payment.PaymentMethodType
import com.loopers.support.enums.payment.PaymentStatusType
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "payment")
class PaymentEntity(
    val orderId: Long,
    val userId: Long,
    @Enumerated(EnumType.STRING)
    val method: PaymentMethodType,
    @Embedded
    val totalPrice: Price,
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    var status: PaymentStatusType = PaymentStatusType.PENDING
        private set

    fun complete() {
        status = PaymentStatusType.COMPLETED
    }

    fun fail() {
        status = PaymentStatusType.FAILED
    }

    fun cancel() {
        status = PaymentStatusType.CANCELED
    }

    fun isCompleted(): Boolean {
        return status == PaymentStatusType.COMPLETED
    }
}
