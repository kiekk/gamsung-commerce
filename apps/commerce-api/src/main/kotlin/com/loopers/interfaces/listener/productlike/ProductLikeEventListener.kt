package com.loopers.interfaces.listener.productlike

import com.loopers.domain.productlike.ProductLikeCountService
import com.loopers.event.payload.productlike.ProductLikeEvent
import com.loopers.event.payload.productlike.ProductUnlikeEvent
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ProductLikeEventListener(
    private val productLikeCountService: ProductLikeCountService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun handle(event: ProductLikeEvent) {
        log.info("상품 좋아요 이벤트 수신: $event")
        productLikeCountService.increase(event.productId)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun handle(event: ProductUnlikeEvent) {
        log.info("상품 좋아요 취소 이벤트 수신: $event")
        productLikeCountService.decrease(event.productId)
    }
}
