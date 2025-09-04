package com.loopers.domain.catalog.handler

import com.loopers.domain.catalog.CatalogEventHandler
import com.loopers.event.EventType
import com.loopers.event.payload.stock.StockSoldOutEvent
import com.loopers.support.cache.CacheNames
import com.loopers.support.cache.CacheRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class StockSoldOutEventHandler(
    private val cacheRepository: CacheRepository,
) : CatalogEventHandler<StockSoldOutEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(eventPayload: StockSoldOutEvent) {
        log.info("[StockSoldOutEventHandler.handle] eventPayload: $eventPayload")
        cacheRepository.evict(CacheNames.PRODUCT_DETAIL_V1 + eventPayload.productId)
    }

    override fun supports(eventType: EventType): Boolean {
        return EventType.PRODUCT_STOCK_SOLD_OUT == eventType
    }
}
