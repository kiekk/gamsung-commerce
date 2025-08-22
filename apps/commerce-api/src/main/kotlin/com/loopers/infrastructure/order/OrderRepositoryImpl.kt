package com.loopers.infrastructure.order

import com.loopers.domain.order.OrderEntity
import com.loopers.domain.order.OrderRepository
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(
    private val orderJpaRepository: OrderJpaRepository,
) : OrderRepository {

    override fun save(order: OrderEntity): OrderEntity {
        return orderJpaRepository.save(order)
    }

    override fun findWithItemsById(id: Long): OrderEntity? {
        return orderJpaRepository.findWithItemsById(id)
    }

    override fun findWithItemsByOrderKey(orderKey: String): OrderEntity? {
        return orderJpaRepository.findWithItemsByOrderKey(orderKey)
    }

    override fun findPendingOrders(): List<OrderEntity> {
        return orderJpaRepository.findPendingOrders()
    }
}
