package com.loopers.domain.order

import com.loopers.application.order.OrderValidator
import com.loopers.domain.coupon.IssuedCouponDiscountAmountCalculator
import com.loopers.domain.product.ProductRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val orderValidator: OrderValidator,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val issuedCouponDiscountAmountCalculator: IssuedCouponDiscountAmountCalculator,
) {
    private val orderTotalPriceCalculator: OrderTotalPriceCalculator = OrderTotalPriceCalculator()

    @Transactional
    fun createOrder(command: OrderCommand.Create): OrderEntity {
        orderValidator.validate(command)

        val products = productRepository.findByIds(command.orderItems.map { it.productId })
        val discountAmount = issuedCouponDiscountAmountCalculator.calculateDiscountAmount(
            command.issuedCouponId,
            orderTotalPriceCalculator.calculateTotalPrice(command.orderItems, products),
        )

        val order = command.toOrderEntity(discountAmount)
        order.addItems(command.toOrderItemEntities(order, products))
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
