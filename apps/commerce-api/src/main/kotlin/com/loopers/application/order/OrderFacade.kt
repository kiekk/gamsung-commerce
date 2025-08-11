package com.loopers.application.order

import com.loopers.domain.coupon.IssuedCouponDiscountAmountCalculator
import com.loopers.domain.coupon.IssuedCouponService
import com.loopers.domain.order.OrderService
import com.loopers.domain.order.OrderTotalPriceCalculator
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.payment.processor.PaymentProcessorCommand
import com.loopers.domain.payment.processor.factory.PaymentProcessorFactory
import com.loopers.domain.product.ProductService
import com.loopers.domain.stock.StockService
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.support.error.payment.StockDeductionFailedException
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
    private val issuedCouponDiscountAmountCalculator: IssuedCouponDiscountAmountCalculator,
) {
    private val orderTotalPriceCalculator: OrderTotalPriceCalculator = OrderTotalPriceCalculator()

    private val log = LoggerFactory.getLogger(OrderFacade::class.java)

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

        orderValidator.validate(criteria)

        val products = productService.getProductsByIds(criteria.orderItems.map { it.productId })
        issuedCouponService.useIssuedCoupon(criteria.issuedCouponId)

        val discountAmount = issuedCouponDiscountAmountCalculator.calculateDiscountAmount(
            criteria.issuedCouponId,
            orderTotalPriceCalculator.calculateTotalPrice(criteria.orderItems, products),
        )

        val createdOrder = orderService.createOrder(criteria.toOrderCommand(user.id, products, discountAmount))
        val createdPayment = paymentService.createPayment(
            PaymentCommand.Create(createdOrder.id, criteria.paymentMethodType, createdOrder.amount),
        )

        paymentProcessorFactory.pay(
            PaymentProcessorCommand.Pay(
                user.id,
                createdPayment.id,
                createdPayment.method,
            ),
        )

        try {
            stockService.deductStockQuantities(criteria.toStockDeductCommands())
            orderService.completeOrder(createdOrder.id)
        } catch (e: StockDeductionFailedException) {
            log.error(e.message, e)
            // 재고 감소 오류에 따른 결제 취소, 포인트 복구
            paymentProcessorFactory.cancel(
                PaymentProcessorCommand.Cancel(
                    user.id,
                    createdPayment.id,
                    createdPayment.method,
                ),
            )
            // 쿠폰 사용 취소
            issuedCouponService.unUseIssuedCoupon(criteria.issuedCouponId)

            // 주문 취소 상태 변경
            orderService.cancelOrder(createdOrder.id)
        }

        return createdOrder.id
    }
}
