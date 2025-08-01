package com.loopers.domain.order

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {
    @Transactional
    fun createOrder(command: OrderCommand.Create): OrderEntity {
        val order = command.toOrderEntity()
        order.addItems(command.toOrderItemEntities(order))
        return orderRepository.save(order)
    }

    @Transactional(readOnly = true)
    fun findWithItemsById(id: Long): OrderEntity? {
        return orderRepository.findWithItemsById(id)
    }

    @Transactional
    fun cancelOrder(id: Long) {
        val order = orderRepository.findWithItemsById(id)
            ?: throw CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다. id: $id")

        order.cancel()
    }

    @Transactional
    fun completeOrder(id: Long) {
        val order = orderRepository.findWithItemsById(id)
            ?: throw CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다. id: $id")

        order.complete()
    }
}
