package com.loopers.domain.productrank

import com.loopers.event.EventType
import com.loopers.event.payload.EventPayload
import com.loopers.support.cache.CacheKey
import org.springframework.stereotype.Service

@Service
class ProductRankService(
    private val productRankEventHandlerFactory: ProductRankEventHandlerFactory,
) {
    fun handleEvent(cacheKey: CacheKey, eventType: EventType, events: List<EventPayload>) {
        productRankEventHandlerFactory.handle(cacheKey, eventType, events)
    }
}
