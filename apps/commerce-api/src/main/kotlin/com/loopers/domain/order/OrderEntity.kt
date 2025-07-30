package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import com.loopers.domain.order.vo.OrderCustomer
import com.loopers.domain.order.vo.OrderItems
import com.loopers.domain.vo.Price

class OrderEntity(
    val userId: Long,
    val orderCustomer: OrderCustomer,
    orderItems: OrderItems,
) : BaseEntity() {
    var orderStatus: OrderStatusType
        private set
    val totalPrice: Price = orderItems.totalPrice()
    val amount: Price = orderItems.amount()

    enum class OrderStatusType {
        PENDING,
        COMPLETED,
        CANCELED,
    }

    init {
        orderStatus = OrderStatusType.PENDING
    }

    fun complete() {
        orderStatus = OrderStatusType.COMPLETED
    }

    fun cancel() {
        orderStatus = OrderStatusType.CANCELED
    }
}
