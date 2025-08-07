package com.loopers.application.order

import com.loopers.domain.coupon.CouponEntity
import com.loopers.domain.coupon.CouponService
import com.loopers.domain.coupon.IssuedCouponService
import com.loopers.domain.coupon.IssuedCouponValidationService
import com.loopers.domain.coupon.policy.factory.CouponDiscountPolicyFactory
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
import com.loopers.support.error.payment.PaymentException
import org.slf4j.LoggerFactory
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
    private val issuedCouponValidationService: IssuedCouponValidationService,
    private val issuedCouponService: IssuedCouponService,
    private val couponService: CouponService,
    private val couponDiscountPolicyFactory: CouponDiscountPolicyFactory,
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

        criteria.orderItems.forEach { orderItem ->
            productValidationService.validate(orderItem.productId)
            stockValidationService.validate(orderItem.productId, orderItem.quantity.value)
            criteria.issuedCouponId?.let { issuedCouponValidationService.validate(it) }
        }

        var coupon: CouponEntity? = null
        criteria.issuedCouponId?.let {
            val issuedCoupon = issuedCouponService.findIssuedCouponById(it)
            issuedCouponService.useIssuedCoupon(it)
            issuedCoupon?.let {
                coupon = couponService.findCouponById(it.couponId)
            }
        }

        var discountAmount: Long = 0L
        coupon?.let {
            val totalAmount = criteria.orderItems.map { it.amount.value }.sumOf { it }
            discountAmount = couponDiscountPolicyFactory.calculateDiscountAmount(coupon, totalAmount)
        }
        val createdOrder = orderService.createOrder(criteria.toCommand(discountAmount))
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
