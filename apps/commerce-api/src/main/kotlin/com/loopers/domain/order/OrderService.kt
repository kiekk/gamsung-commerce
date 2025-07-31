package com.loopers.domain.order

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {
    @Transactional
    fun createOrder(command: OrderCommand.Create) : OrderEntity{
        val order = command.toOrderEntity()
        order.addItems(command.toOrderItemEntities(order))
        return orderRepository.save(order)
    }

    @Transactional(readOnly = true)
    fun findOrderBy(id: Long): OrderEntity? {
        return orderRepository.findById(id)
    }
}
