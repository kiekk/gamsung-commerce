package com.loopers.domain.order

import com.loopers.domain.order.vo.OrderCustomer
import com.loopers.domain.order.vo.OrderCustomerFixture.Companion.anOrderCustomer

class OrderEntityFixture {
    private var userId = 1L
    private var orderCustomer = anOrderCustomer().build()

    companion object {
        fun anOrder(): OrderEntityFixture = OrderEntityFixture()
    }

    fun userId(userId: Long): OrderEntityFixture = apply { this.userId = userId }

    fun orderCustomer(orderCustomer: OrderCustomer): OrderEntityFixture = apply { this.orderCustomer = orderCustomer }

    fun build(): OrderEntity = OrderEntity(
        userId,
        orderCustomer,
    )
}
