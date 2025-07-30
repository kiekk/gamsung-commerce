package com.loopers.domain.payment.vo

import com.loopers.domain.payment.PaymentItemEntity
import com.loopers.domain.vo.Price

@JvmInline
value class PaymentItems(
    val items: List<PaymentItemEntity>,
) {

    init {
        require(items.isNotEmpty()) { "주문 항목 목록은 비어있을 수 없습니다." }
    }

    fun totalAmount(): Price {
        return Price(items.sumOf { it.amount.value })
    }
}
