package com.loopers.domain.order

import com.loopers.domain.order.OrderItemEntityFixture.Companion.anOrderItem
import com.loopers.domain.order.vo.OrderCustomer
import com.loopers.domain.order.vo.OrderCustomerFixture.Companion.anOrderCustomer
import com.loopers.domain.order.vo.OrderItems

class OrderEntityFixture {
    private var userId = 1L
    private var orderCustomer = anOrderCustomer().build()
    private var orderItems = OrderItems(
        listOf(
            anOrderItem().build(),
            anOrderItem().build(),
        ),
    )

    companion object {
        fun anOrder(): OrderEntityFixture = OrderEntityFixture()
    }

    fun userId(userId: Long): OrderEntityFixture = apply { this.userId = userId }

    fun orderCustomer(orderCustomer: OrderCustomer): OrderEntityFixture = apply { this.orderCustomer = orderCustomer }

    fun orderItems(orderItems: OrderItems): OrderEntityFixture = apply { this.orderItems = orderItems }

    fun build(): OrderEntity = OrderEntity(
        userId,
        orderCustomer,
        orderItems,
    )
}
