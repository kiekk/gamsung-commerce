package com.loopers.domain.productrank

import com.loopers.event.Event
import com.loopers.event.payload.EventPayload
import com.loopers.support.cache.CacheKey
import org.springframework.stereotype.Service

@Service
class ProductRankService(
    private val productRankEventHandlerFactory: ProductRankEventHandlerFactory,
) {
    fun handleEvent(cacheKey: CacheKey, event: Event<out EventPayload>) {
        productRankEventHandlerFactory.handle(cacheKey, event)
    }
}
