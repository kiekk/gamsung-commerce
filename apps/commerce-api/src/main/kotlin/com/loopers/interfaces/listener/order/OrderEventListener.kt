package com.loopers.interfaces.listener.order

import com.loopers.domain.coupon.IssuedCouponService
import com.loopers.domain.order.OrderService
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockEventPublisher
import com.loopers.domain.stock.StockService
import com.loopers.event.payload.order.OrderCompletedEvent
import com.loopers.event.payload.order.OrderFailedSuccessEvent
import com.loopers.event.payload.payment.PaymentCompletedEvent
import com.loopers.event.payload.payment.PaymentFailedEvent
import com.loopers.event.payload.payment.PaymentFailedSuccessEvent
import com.loopers.event.payload.stock.StockAdjustedEvent
import com.loopers.event.publisher.EventPublisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class OrderEventListener(
    private val orderService: OrderService,
    private val issuedCouponService: IssuedCouponService,
    private val stockService: StockService,
    private val eventPublisher: EventPublisher,
    private val stockEventPublisher: StockEventPublisher,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /*
    주문 완료
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleBeforeIssuedCoupon(event: OrderCompletedEvent) {
        log.info("주문 완료 이벤트 수신(커밋 전): $event")
        val order = orderService.findWithItemsByOrderKey(event.orderKey)
            ?: throw IllegalStateException("주문 정보를 찾을 수 없습니다. orderKey: ${event.orderKey}")
        issuedCouponService.useIssuedCoupon(order.issuedCouponId)
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleBeforeStock(event: OrderCompletedEvent) {
        log.info("주문 완료 이벤트 수신(커밋 전): $event")
        val order = orderService.findWithItemsByOrderKey(event.orderKey)
            ?: throw IllegalStateException("주문 정보를 찾을 수 없습니다. orderKey: ${event.orderKey}")
        stockService.deductStockQuantities(
            StockCommand.Deduct.from(
                order.orderItems.toProductQuantityMap(),
            ),
        )
    }

    /*
    결제 완료
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: PaymentCompletedEvent) {
        log.info("결제 완료 이벤트 수신: $event")
        // 주문 완료 이벤트 발행 - 마커용
        eventPublisher.publish(OrderCompletedEvent(event.orderKey, event.transactionKey))
        val order = orderService.findWithItemsByOrderKey(event.orderKey)
            ?: throw IllegalStateException("주문 정보를 찾을 수 없습니다. orderKey: ${event.orderKey}")
        orderService.completeOrder(order.id)
    }

    /*
    주문 완료
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAfterEvent(event: OrderCompletedEvent) {
        log.info("[OrderEventListener.handleAfterEvent] event: $event")
        val order = orderService.findWithItemsByOrderKey(event.orderKey)
            ?: throw IllegalStateException("주문 정보를 찾을 수 없습니다. orderKey: ${event.orderKey}")
        order.orderItems.toProductQuantityMap().forEach { productQuantity ->
            stockEventPublisher.publish(StockAdjustedEvent(productQuantity.key, productQuantity.value))
        }
    }

    /*
    주문 완료 롤백
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    fun handle(event: OrderCompletedEvent) {
        log.info("주문 완료 롤백 이벤트 수신: $event")
        // 주문 복구 완료 이벤트 발행 - 마커용
        eventPublisher.publish(OrderFailedSuccessEvent(event.orderKey, event.transactionKey))
        // 주문 실패 상태 변경
        orderService.failOrderByOrderKey(event.orderKey)
    }

    /*
    결제 실패
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: PaymentFailedEvent) {
        log.info("결제 실패 이벤트 수신: $event")
        // 결제 실패 완료 이벤트 발행 - 마커용
        eventPublisher.publish(PaymentFailedSuccessEvent(event.orderKey, event.transactionKey))
        orderService.failOrderByOrderKey(event.orderKey)
    }

    /*
    주문 실패 완료
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleBeforeFailOrderIssuedCoupon(event: OrderFailedSuccessEvent) {
        log.info("주문 실패 완료 이벤트 수신(커밋 전): $event")
        val order = orderService.findWithItemsByOrderKey(event.orderKey)
            ?: throw IllegalStateException("주문 정보를 찾을 수 없습니다. orderKey: ${event.orderKey}")
        // 쿠폰 사용 취소
        issuedCouponService.unUseIssuedCoupon(order.issuedCouponId)
    }
}
