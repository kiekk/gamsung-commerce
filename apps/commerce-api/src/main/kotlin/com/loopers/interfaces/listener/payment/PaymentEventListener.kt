package com.loopers.interfaces.listener.payment

import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.vo.Price
import com.loopers.event.payload.order.OrderCreatedEvent
import com.loopers.event.payload.order.OrderFailedSuccessEvent
import com.loopers.event.payload.payment.PaymentFailedSuccessEvent
import com.loopers.support.enums.payment.PaymentCardType
import com.loopers.support.enums.payment.PaymentMethodType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentEventListener(
    private val paymentService: PaymentService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    /*
    주문 생성
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleAfterPay(event: OrderCreatedEvent) {
        log.info("주문 생성 이벤트 수신: $event")
        paymentService.pay(
            PaymentCommand.Pay(
                event.orderId,
                event.userId,
                PaymentMethodType.valueOf(event.paymentMethod),
                Price(event.totalPrice),
                event.cardType?.let { PaymentCardType.valueOf(it) },
                event.cardNo,
                event.orderKey,
            ),
        )
    }

    /*
    결제 실패 완료
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleBeforeFailIssuedCoupon(event: PaymentFailedSuccessEvent) {
        log.info("결제 실패 완료 이벤트 수신(커밋 전): $event")
        paymentService.failPayment(event.transactionKey)
    }

    /*
    주문 실패 완료
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleBeforeFailOrder(event: OrderFailedSuccessEvent) {
        log.info("주문 실패 완료 이벤트 수신(커밋 전): $event")
        val payment = paymentService.findByTransactionKey(event.transactionKey)
            ?: throw IllegalStateException("결제 정보를 찾을 수 없습니다. transactionKey: ${event.transactionKey}")
        // 재고 감소 오류에 따른 결제 취소
        paymentService.cancel(
            PaymentCommand.Cancel(
                payment.userId,
                payment.id,
                payment.method,
            ),
        )
    }
}
