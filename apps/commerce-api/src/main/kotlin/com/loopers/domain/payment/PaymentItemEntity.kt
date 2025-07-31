package com.loopers.domain.payment

import com.loopers.domain.BaseEntity
import com.loopers.domain.vo.Price
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "payment_item")
class PaymentItemEntity(
    @ManyToOne
    val payment: PaymentEntity,
    val orderItemId: Long,
    @Embedded
    val amount: Price,
) : BaseEntity() {
    @Enumerated(EnumType.STRING)
    var status: PaymentItemStatusType = PaymentItemStatusType.PENDING
        private set

    enum class PaymentItemStatusType {
        PENDING,
        COMPLETED,
        FAILED,
    }

    fun complete() {
        status = PaymentItemStatusType.COMPLETED
    }

    fun fail() {
        status = PaymentItemStatusType.FAILED
    }

    fun isPending(): Boolean {
        return status == PaymentItemStatusType.PENDING
    }

    fun isCompleted(): Boolean {
        return status == PaymentItemStatusType.COMPLETED
    }

    fun isFailed(): Boolean {
        return status == PaymentItemStatusType.FAILED
    }

}
