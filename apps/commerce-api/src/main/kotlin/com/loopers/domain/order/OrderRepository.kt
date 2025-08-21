package com.loopers.domain.order

interface OrderRepository {
    fun save(order: OrderEntity): OrderEntity

    fun findWithItemsById(id: Long): OrderEntity?

    fun findWithItemsByOrderKey(orderKey: String): OrderEntity?
}
