package com.loopers.application.order

import com.loopers.domain.order.OrderService
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.processor.PaymentProcessorCommand
import com.loopers.domain.payment.processor.factory.PaymentProcessorFactory
import com.loopers.domain.product.ProductValidationService
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockService
import com.loopers.domain.stock.StockValidationService
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderFacade(
    private val userService: UserService,
    private val stockService: StockService,
    private val orderService: OrderService,
    private val paymentService: PaymentService,
    private val paymentProcessorFactory: PaymentProcessorFactory,
    private val productValidationService: ProductValidationService,
    private val stockValidationService: StockValidationService,
) {
    /*
        TODO: 3주차에는 재고 차감 예외 발생 시 포인트 원복 및 결제/주문 실패 처리로 구현하였으나,
        4주차에는 재고 차감 예외 발생 시 모두 롤백처리로 구현해야 하므로 해당 테스트 케이스를 주석 처리.
        추후 다시 원복 시나리오로 구현할 경우 로직 수정 및 주석 해제 필요
         */
    @Transactional(readOnly = true)
    fun getOrderById(criteria: OrderCriteria.Get): OrderInfo.OrderDetail {
        userService.findUserBy(criteria.userId) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "사용자를 찾을 수 없습니다. userId: ${criteria.userId}",
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
        val user = userService.findUserBy(criteria.userId) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "사용자를 찾을 수 없습니다. userId: ${criteria.userId}",
        )

        criteria.orderItems.forEach { orderItem ->
            productValidationService.validate(orderItem.productId)
            stockValidationService.validate(orderItem.productId, orderItem.quantity.value)
        }

        val createdOrder = orderService.createOrder(criteria.toCommand())
        val createdPayment = paymentService.createPayment(
            PaymentCommand.Create(
                createdOrder.id,
                criteria.paymentMethodType,
                createdOrder.orderItems.items.map {
                    PaymentCommand.Create.PaymentItemCommand(
                        it.id,
                        it.amount,
                    )
                },
            ),
        )

        paymentProcessorFactory.process(
            PaymentProcessorCommand.Process(
                user.id,
                createdPayment.id,
                criteria.paymentMethodType,
            ),
        )

        stockService.deductStockQuantities(
            criteria.orderItems.map {
                StockCommand.Decrease(
                    it.productId,
                    it.quantity.value,
                )
            },
        )

        orderService.completeOrder(createdOrder.id)

        return createdOrder.id
    }
}
