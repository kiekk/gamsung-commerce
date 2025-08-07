package com.loopers.application.order

import com.loopers.domain.coupon.IssuedCouponDiscountCalculator
import com.loopers.domain.coupon.IssuedCouponService
import com.loopers.domain.order.OrderService
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.processor.PaymentProcessorCommand
import com.loopers.domain.payment.processor.factory.PaymentProcessorFactory
import com.loopers.domain.product.ProductService
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockService
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.support.error.payment.PaymentException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderFacade(
    private val userService: UserService,
    private val productService: ProductService,
    private val stockService: StockService,
    private val orderService: OrderService,
    private val paymentService: PaymentService,
    private val paymentProcessorFactory: PaymentProcessorFactory,
    private val orderValidator: OrderValidator,
    private val issuedCouponService: IssuedCouponService,
    private val issuedCouponDiscountCalculator: IssuedCouponDiscountCalculator,
) {

    private val log = LoggerFactory.getLogger(OrderFacade::class.java)

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

        orderValidator.validate(criteria)

        val products = productService.getProductsByIds(criteria.orderItems.map { it.productId })
        val totalPrice = criteria.orderItems.map { orderItem ->
            products.find { product -> product.id == orderItem.productId }?.price?.value?.times(orderItem.quantity.value) ?: 0L
        }.sumOf { it }
        issuedCouponService.useIssuedCoupon(criteria.issuedCouponId)
        val discountAmount = issuedCouponDiscountCalculator.calculate(
            criteria.issuedCouponId,
            totalPrice,
        )

        val createdOrder = orderService.createOrder(criteria.toCommand(products, discountAmount))
        val createdPayment = paymentService.createPayment(
            PaymentCommand.Create(createdOrder.id, criteria.paymentMethodType, createdOrder.amount),
        )

        paymentProcessorFactory.pay(
            PaymentProcessorCommand.Pay(
                user.id,
                createdPayment.id,
                criteria.paymentMethodType,
            ),
        )

        try {
            stockService.deductStockQuantities(
                criteria.orderItems.map {
                    StockCommand.Decrease(
                        it.productId,
                        it.quantity.value,
                    )
                },
            )
            orderService.completeOrder(createdOrder.id)
        } catch (e: PaymentException) {
            log.error(e.message, e)
            // 재고 감소 오류에 따른 결제 취소, 포인트 복구
            paymentProcessorFactory.cancel(
                PaymentProcessorCommand.Cancel(
                    user.id,
                    createdPayment.id,
                    criteria.paymentMethodType,
                ),
            )
            // 주문 취소 상태 변경
            orderService.cancelOrder(createdOrder.id)
        }

        return createdOrder.id
    }
}
