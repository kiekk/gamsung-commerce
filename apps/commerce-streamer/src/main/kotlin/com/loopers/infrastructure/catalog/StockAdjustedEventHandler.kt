package com.loopers.infrastructure.catalog

import com.loopers.domain.catalog.CatalogEventHandler
import com.loopers.event.EventType
import com.loopers.event.payload.stock.StockAdjustedEvent
import com.loopers.support.cache.CacheNames
import com.loopers.support.cache.CacheRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class StockAdjustedEventHandler(
    private val cacheRepository: CacheRepository,
) : CatalogEventHandler<StockAdjustedEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(eventPayload: StockAdjustedEvent) {
        log.info("[StockAdjustedEventHandler.handle] eventPayload: $eventPayload")
        cacheRepository.evict(CacheNames.PRODUCT_DETAIL_V1 + eventPayload.productId)
    }

    override fun supports(eventType: EventType): Boolean {
        return EventType.PRODUCT_STOCK_ADJUSTED == eventType
    }
}
