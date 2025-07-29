package com.loopers.domain.order.vo

import com.loopers.domain.order.OrderItemEntity
import com.loopers.domain.vo.Price

@JvmInline
value class OrderItems(
    val value: List<OrderItemEntity>,
) {

    init {
        require(value.isNotEmpty()) { "주문 항목은 비어 있을 수 없습니다." }
    }

    fun totalPrice(): Price {
        return Price(value.sumOf { it.totalPrice.value })
    }

    fun amount(): Price {
        return Price(value.sumOf { it.amount.value })
    }

    fun size(): Int {
        return value.size
    }
}
