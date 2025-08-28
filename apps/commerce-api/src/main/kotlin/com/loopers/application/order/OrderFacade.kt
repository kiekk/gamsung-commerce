package com.loopers.application.order

import com.loopers.domain.order.OrderService
import com.loopers.domain.user.UserService
import com.loopers.event.payload.order.OrderCreatedEvent
import com.loopers.event.publisher.EventPublisher
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderFacade(
    private val userService: UserService,
    private val orderService: OrderService,
    private val eventPublisher: EventPublisher,
) {
    private val log = LoggerFactory.getLogger(OrderFacade::class.java)

    fun findPendingOrders(): List<OrderInfo.OrderDetail> {
        return orderService.findPendingOrders()
            .map { OrderInfo.OrderDetail.from(it) }
    }

    @Transactional(readOnly = true)
    fun getOrder(criteria: OrderCriteria.Get): OrderInfo.OrderDetail {
        userService.findUserBy(criteria.username) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "사용자를 찾을 수 없습니다. username: ${criteria.username}",
        )
        return orderService.findWithItemsById(criteria.orderId)?.let { orderEntity ->
            OrderInfo.OrderDetail.from(orderEntity)
        } ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "주문을 찾을 수 없습니다. orderId: ${criteria.orderId}",
        )
    }

    @Transactional
    fun placeOrder(criteria: OrderCriteria.Create): Long {
        val user = userService.findUserBy(criteria.username) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "사용자를 찾을 수 없습니다. username: ${criteria.username}",
        )

        val createdOrder = orderService.createOrder(criteria.toOrderCommand(user.id))

        // 주문 생성 이벤트 발행
        eventPublisher.publish(
            OrderCreatedEvent(
                createdOrder.id,
                user.id,
                createdOrder.amount.value,
                criteria.paymentMethodType.name,
                criteria.cardType?.name,
                criteria.cardNo,
                createdOrder.orderKey,
            ),
        )

        return createdOrder.id
    }
}
