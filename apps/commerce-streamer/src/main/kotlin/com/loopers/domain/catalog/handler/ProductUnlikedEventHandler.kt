package com.loopers.domain.catalog.handler

import com.loopers.domain.catalog.CatalogEventHandler
import com.loopers.event.EventType
import com.loopers.event.payload.productlike.ProductUnlikedEvent
import com.loopers.support.cache.CacheNames
import com.loopers.support.cache.CacheRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProductUnlikedEventHandler(
    private val cacheRepository: CacheRepository,
) : CatalogEventHandler<ProductUnlikedEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(eventPayload: ProductUnlikedEvent) {
        log.info("[ProductUnlikedEventHandler.handle] eventPayload: $eventPayload")
        cacheRepository.evict(CacheNames.PRODUCT_LIKE_COUNT_V1 + eventPayload.productId)
    }

    override fun supports(eventType: EventType): Boolean {
        return EventType.PRODUCT_UNLIKED == eventType
    }
}
