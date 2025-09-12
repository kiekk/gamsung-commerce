package com.loopers.domain.productrank.handler

import com.loopers.domain.productrank.ProductRankEventHandler
import com.loopers.domain.productrank.ProductRankScoreCalculator
import com.loopers.event.EventType
import com.loopers.event.payload.productlike.ProductLikedEvent
import com.loopers.support.cache.CacheKey
import com.loopers.support.cache.CacheRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ProductRankProductLikedEventHandler(
    private val cacheRepository: CacheRepository,
) : ProductRankEventHandler<ProductLikedEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(cacheKey: CacheKey, events: List<ProductLikedEvent>) {
        events.groupingBy { it.productId }.eachCount()
            .forEach { (productId, count) ->
                val score = ProductRankScoreCalculator.calculateScoreByLikeCount(count)
                log.info(
                    "[ProductRankProductLikedEventHandler.handle] productId: $productId," +
                            " count: $count, cacheKey: $cacheKey, score: ${score * count}",
                )
                cacheRepository.zIncrBy(cacheKey, productId, score)
            }
    }

    override fun supports(eventType: EventType): Boolean {
        return EventType.PRODUCT_LIKED == eventType
    }
}
