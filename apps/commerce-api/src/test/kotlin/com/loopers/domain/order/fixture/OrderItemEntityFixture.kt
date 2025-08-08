package com.loopers.domain.order.fixture

import com.loopers.domain.order.OrderEntity
import com.loopers.domain.order.OrderItemEntity
import com.loopers.domain.vo.Price

class OrderItemEntityFixture {
    private var order: OrderEntity = OrderEntityFixture.Companion.anOrder().build()
    private var productId = 1L
    private var productName = "상품A"
    private var amount = Price(1000L)

    companion object {
        fun anOrderItem(): OrderItemEntityFixture = OrderItemEntityFixture()
    }

    fun order(order: OrderEntity): OrderItemEntityFixture = apply { this.order = order }

    fun productId(productId: Long): OrderItemEntityFixture = apply { this.productId = productId }

    fun productName(productName: String): OrderItemEntityFixture = apply { this.productName = productName }

    fun amount(amount: Price): OrderItemEntityFixture = apply { this.amount = amount }

    fun build(): OrderItemEntity = OrderItemEntity(
        order,
        productId,
        productName,
        amount,
    )
}
