package com.loopers.interfaces.listener.productlike

import com.loopers.domain.productlike.ProductLikeCountService
import com.loopers.event.payload.productlike.ProductLikeEvent
import com.loopers.event.payload.productlike.ProductUnlikeEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ProductLikeEventHandler(
    private val productLikeCountService: ProductLikeCountService,
) {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun handle(event: ProductLikeEvent) {
        productLikeCountService.increase(event.productId)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    fun handle(event: ProductUnlikeEvent) {
        productLikeCountService.decrease(event.productId)
    }
}
