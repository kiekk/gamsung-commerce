package com.loopers.application.order

import com.loopers.domain.coupon.IssuedCouponService
import com.loopers.domain.order.OrderService
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockService
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderFacade(
    private val userService: UserService,
    private val stockService: StockService,
    private val orderService: OrderService,
    private val paymentService: PaymentService,
    private val issuedCouponService: IssuedCouponService,
) {
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

        val createdOrder = orderService.createOrder(criteria.toOrderCommand(user.id))
        issuedCouponService.useIssuedCoupon(criteria.issuedCouponId)

        val payment = paymentService.pay(
            PaymentCommand.Pay(
                createdOrder.id,
                user.id,
                criteria.paymentMethodType,
                createdOrder.amount,
            ),
        )

        // POINT 결제인 경우 결제 상태는 COMPLETED이므로 주문 완료 처리 로직 추가
        // CARD(비동기) 결제인 경우 결제 상태는 PENDING이므로 주문 완료 처리 로직 추가 X
        // CARD(동기) 결제인 경우 결제 상태는 COMPLETED이므로 주문 완료 처리 로직 추가
        if (payment.isCompleted()) {
            handlePaymentCompleted(createdOrder.id, payment.id)
        }

        return createdOrder.id
    }

    @Transactional
    fun handlePaymentCompleted(orderId: Long, paymentId: Long) {
        val order = orderService.findWithItemsById(orderId) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "주문을 찾을 수 없습니다. orderId: $orderId",
        )

        if (order.isCompleted()) {
            log.warn("이미 완료된 주문입니다. orderId: $orderId")
            return
        }

        val payment = paymentService.findById(paymentId) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "결제 정보를 찾을 수 없습니다. paymentId: $paymentId",
        )

        if (!payment.isCompleted()) {
            log.warn("완료되지 않은 결제 입니다. paymentId: $paymentId, status: ${payment.status}")
            return
        }

        try {
            stockService.deductStockQuantities(
                StockCommand.Deduct.from(
                    order.orderItems.toProductQuantityMap(),
                ),
            )
            orderService.completeOrder(orderId)
            log.info("주문이 완료되었습니다. orderId: $orderId")
        } catch (e: Exception) {
            log.error(e.message, e)

            // 재고 감소 오류에 따른 결제 취소
            paymentService.cancel(
                PaymentCommand.Cancel(
                    order.userId,
                    paymentId,
                    payment.method,
                ),
            )
            // 쿠폰 사용 취소
            issuedCouponService.unUseIssuedCoupon(order.issuedCouponId)

            // 주문 실패 상태 변경
            orderService.cancelOrder(order.id)
        }
    }
}
