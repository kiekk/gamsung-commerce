package com.loopers.domain.order.fixture

import com.loopers.domain.order.OrderEntity
import com.loopers.domain.order.vo.OrderCustomer
import com.loopers.domain.order.vo.OrderCustomerFixture

class OrderEntityFixture {
    private var userId = 1L
    private var orderCustomer = OrderCustomerFixture.Companion.anOrderCustomer().build()

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
