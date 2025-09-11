package com.loopers.domain.productrank.handler

import com.loopers.domain.productrank.ProductRankEventHandler
import com.loopers.domain.productrank.ProductRankScoreCalculator
import com.loopers.event.EventType
import com.loopers.event.payload.stock.StockAdjustedEvent
import com.loopers.support.cache.CacheKey
import com.loopers.support.cache.CacheRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProductRankStockAdjustedEventHandler(
    private val cacheRepository: CacheRepository,
) : ProductRankEventHandler<StockAdjustedEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(cacheKey: CacheKey, event: StockAdjustedEvent) {
        val score = ProductRankScoreCalculator.calculateScoreBySalesCount(event.quantity, event.amount)
        log.info("[ProductLikedEventHandler.handle] event: $event, cacheKey: $cacheKey, score: $score")
        cacheRepository.zIncrBy(cacheKey, event.productId, score)
    }

    override fun supports(eventType: EventType): Boolean {
        return EventType.PRODUCT_STOCK_ADJUSTED == eventType
    }
}
