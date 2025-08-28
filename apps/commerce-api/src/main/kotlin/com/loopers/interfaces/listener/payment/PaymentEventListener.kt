package com.loopers.interfaces.listener.payment

import com.loopers.application.order.OrderFacade
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentService
import com.loopers.domain.vo.Price
import com.loopers.event.payload.order.OrderCompletedEvent
import com.loopers.event.payload.order.OrderCreatedEvent
import com.loopers.event.payload.payment.PaymentCompletedEvent
import com.loopers.event.payload.payment.PaymentFailedEvent
import com.loopers.support.enums.payment.PaymentCardType
import com.loopers.support.enums.payment.PaymentMethodType
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PaymentEventListener(
    private val orderFacade: OrderFacade,
    private val paymentService: PaymentService,
) {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: OrderCreatedEvent) {
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

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: PaymentCompletedEvent) {
        orderFacade.handlePaymentCompleted(event.orderKey, event.transactionKey)
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handle(event: PaymentFailedEvent) {
        orderFacade.handlePaymentFailed(event.orderKey, event.transactionKey)
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    fun handle(event: OrderCompletedEvent) {
        orderFacade.recoveryOrder(event.orderKey, event.transactionKey)
    }
}
