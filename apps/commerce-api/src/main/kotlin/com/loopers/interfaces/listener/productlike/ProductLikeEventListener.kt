package com.loopers.interfaces.listener.productlike

import com.loopers.domain.productlike.ProductLikeCountService
import com.loopers.domain.productlike.ProductLikeEventPublisher
import com.loopers.event.payload.productlike.ProductLikedEvent
import com.loopers.event.payload.productlike.ProductUnlikedEvent
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ProductLikeEventListener(
    private val productLikeCountService: ProductLikeCountService,
    private val productLikeEventPublisher: ProductLikeEventPublisher,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun handle(event: ProductLikedEvent) {
        log.info("[ProductLikeEventListener.handle] event: $event")
        productLikeCountService.increase(event.productId)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun handleAfterEvent(event: ProductLikedEvent) {
        log.info("[ProductLikeEventListener.handleAfterEvent] event: $event")
        productLikeEventPublisher.publish(event)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun handle(event: ProductUnlikedEvent) {
        log.info("[ProductUnlikeEventListener.handle] event: $event")
        productLikeCountService.decrease(event.productId)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun handleAfterEvent(event: ProductUnlikedEvent) {
        log.info("[ProductUnlikeEventListener.handleAfterEvent] event: $event")
        productLikeEventPublisher.publish(event)
    }
}
