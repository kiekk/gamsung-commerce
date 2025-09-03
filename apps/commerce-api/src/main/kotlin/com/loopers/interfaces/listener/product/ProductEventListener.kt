package com.loopers.interfaces.listener.product

import com.loopers.domain.product.ProductEventPublisher
import com.loopers.event.payload.product.ProductChangedEvent
import com.loopers.event.payload.product.ProductViewedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ProductEventListener(
    private val productEventPublisher: ProductEventPublisher,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @EventListener
    fun handle(event: ProductViewedEvent) {
        log.info("상품 조회 이벤트 수신: $event")
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun handleAfterEvent(event: ProductChangedEvent) {
        log.info("[ProductEventListener.handleAfterEvent] event: $event")
        productEventPublisher.publish(event)
    }
}
