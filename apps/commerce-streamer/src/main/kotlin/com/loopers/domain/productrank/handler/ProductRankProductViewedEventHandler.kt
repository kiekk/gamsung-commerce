package com.loopers.domain.productrank.handler

import com.loopers.domain.productrank.ProductRankEventHandler
import com.loopers.domain.productrank.ProductRankScoreCalculator
import com.loopers.event.EventType
import com.loopers.event.payload.product.ProductViewedEvent
import com.loopers.support.cache.CacheKey
import com.loopers.support.cache.CacheRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProductRankProductViewedEventHandler(
    private val cacheRepository: CacheRepository,
) : ProductRankEventHandler<ProductViewedEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(cacheKey: CacheKey, event: ProductViewedEvent) {
        val score = ProductRankScoreCalculator.calculateScoreByViewCount(1)
        log.info("[ProductLikedEventHandler.handle] event: $event, cacheKey: $cacheKey, score: $score")
        cacheRepository.zIncrBy(cacheKey, event.productId, score)
    }

    override fun supports(eventType: EventType): Boolean {
        return EventType.PRODUCT_VIEWED == eventType
    }
}
