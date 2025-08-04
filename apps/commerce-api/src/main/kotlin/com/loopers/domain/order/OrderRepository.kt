package com.loopers.domain.order

interface OrderRepository {
    fun save(order: OrderEntity): OrderEntity

    fun findWithItemsById(id: Long): OrderEntity?
}
