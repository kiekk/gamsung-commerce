package com.loopers.domain.order.vo

import com.loopers.domain.order.OrderItemEntity
import com.loopers.domain.vo.Price
import jakarta.persistence.Embeddable

@Embeddable
class OrderItems(
    val items: List<OrderItemEntity>,
) {

    fun amount(): Price {
        return Price(items.sumOf { it.amount.value })
    }

    fun size(): Int {
        return items.size
    }
}
