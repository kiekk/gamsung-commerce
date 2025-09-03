package com.loopers.infrastructure.catalog

import com.loopers.domain.catalog.CatalogEventHandler
import com.loopers.event.EventType
import com.loopers.event.payload.productlike.ProductLikeChangedEvent
import com.loopers.support.cache.CacheNames
import com.loopers.support.cache.CacheRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProductLikeChangedEventHandler(
    private val cacheRepository: CacheRepository,
) : CatalogEventHandler<ProductLikeChangedEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(eventPayload: ProductLikeChangedEvent) {
        log.info("ProductLikeChangedEventHandler - handle: $eventPayload")
        cacheRepository.evict(CacheNames.PRODUCT_LIKE_COUNT_V1 + eventPayload.productId)
    }

    override fun supports(eventType: EventType): Boolean {
        return EventType.PRODUCT_LIKE_CHANGED == eventType
    }
}
