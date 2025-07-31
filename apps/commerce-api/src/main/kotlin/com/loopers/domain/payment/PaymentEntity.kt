package com.loopers.domain.payment

import com.loopers.domain.BaseEntity
import com.loopers.domain.payment.vo.PaymentItems
import com.loopers.domain.vo.Price
import jakarta.persistence.CascadeType
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "payment")
class PaymentEntity(
    val orderId: Long,
    @Enumerated(EnumType.STRING)
    val method: PaymentMethodType,
    @OneToMany(mappedBy = "payment", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    private val _paymentItems: MutableList<PaymentItemEntity> = mutableListOf(),
) : BaseEntity() {
    val paymentItems: PaymentItems
        get() = PaymentItems(_paymentItems)

    @Enumerated(EnumType.STRING)
    var status: PaymentStatusType = PaymentStatusType.PENDING
        private set

    @Embedded
    var totalAmount: Price = paymentItems.totalAmount()
        private set

    enum class PaymentStatusType {
        PENDING,
        COMPLETED,
        FAILED,
    }

    enum class PaymentMethodType {
        POINT,
    }

    fun addItems(paymentItems: List<PaymentItemEntity>) {
        _paymentItems.addAll(paymentItems)
        totalAmount = this.paymentItems.totalAmount()
    }

    fun complete() {
        status = PaymentStatusType.COMPLETED
        paymentItems.complete()
    }

    fun fail() {
        status = PaymentStatusType.FAILED
        paymentItems.fail()
    }
}
