package com.loopers.domain.payment.vo

import com.loopers.domain.payment.PaymentItemEntity
import com.loopers.domain.vo.Price
import jakarta.persistence.Embeddable

@Embeddable
class PaymentItems(
    val items: List<PaymentItemEntity>,
) {

    fun totalAmount(): Price {
        return Price(items.sumOf { it.amount.value })
    }

    fun complete() {
        items.forEach { it.complete() }
    }

    fun fail() {
        items.forEach { it.fail() }
    }

    fun isAllPending(): Boolean {
        return items.all { it.isPending() }
    }

    fun isAllCompleted(): Boolean {
        return items.all { it.isCompleted() }
    }

    fun isAllFailed(): Boolean {
        return items.all { it.isFailed() }
    }

    fun isAllCanceled(): Boolean {
        return items.all { it.isCanceled() }
    }

    fun cancel() {
        items.forEach { it.cancel() }
    }

}
