package com.loopers.infrastructure.catalog

import com.loopers.domain.catalog.CatalogEventHandler
import com.loopers.event.EventType
import com.loopers.event.payload.product.ProductChangedEvent
import com.loopers.support.cache.CacheNames
import com.loopers.support.cache.CacheRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProductChangedEventHandler(
    private val cacheRepository: CacheRepository,
) : CatalogEventHandler<ProductChangedEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(eventPayload: ProductChangedEvent) {
        log.info("ProductChangedEventHandler - handle: $eventPayload")
        cacheRepository.evict(CacheNames.PRODUCT_DETAIL_V1 + eventPayload.productId)
    }

    override fun supports(eventType: EventType): Boolean {
        return EventType.PRODUCT_CHANGED == eventType
    }

}
