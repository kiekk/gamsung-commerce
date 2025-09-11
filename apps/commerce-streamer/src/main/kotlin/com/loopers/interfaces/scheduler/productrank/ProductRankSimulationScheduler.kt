package com.loopers.interfaces.scheduler.productrank

import com.loopers.domain.productrank.ProductRankService
import com.loopers.event.Event
import com.loopers.event.EventType
import com.loopers.event.payload.EventPayload
import com.loopers.event.payload.product.ProductViewedEvent
import com.loopers.event.payload.productlike.ProductLikedEvent
import com.loopers.event.payload.productlike.ProductUnlikedEvent
import com.loopers.event.payload.stock.StockAdjustedEvent
import com.loopers.support.cache.productrank.ProductRankCacheKeyGenerator
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Profile("rank-simulation")
@Component
class ProductRankSimulationScheduler(
    private val productRankService: ProductRankService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "*/1 * * * * *", zone = "Asia/Seoul")
    fun createProductRankByRandomEvent() {
        log.info("[ProductRankScheduler.generateRandomEvent] start")

        // Daily
        productRankService.handleEvent(
            ProductRankCacheKeyGenerator.generate(LocalDate.now()),
            generateRandomEvent(),
        )

        // Hourly
        productRankService.handleEvent(
            ProductRankCacheKeyGenerator.generate(LocalDateTime.now()),
            generateRandomEvent(),
        )
        log.info("[ProductRankScheduler.generateRandomEvent] end")
    }

    fun generateRandomEvent(): Event<out EventPayload> {
        return when ((1..4).random()) {
            1 -> Event(
                UUID.randomUUID().toString(),
                EventType.PRODUCT_STOCK_ADJUSTED,
                StockAdjustedEvent(
                    (1..30).random().toLong(),
                    (1..10).random(),
                    (1000..10000).random().toLong(),
                ),
            )

            2 -> Event(
                UUID.randomUUID().toString(),
                EventType.PRODUCT_LIKED,
                ProductLikedEvent(
                    (1..30).random().toLong(),
                ),
            )

            3 -> Event(
                UUID.randomUUID().toString(),
                EventType.PRODUCT_UNLIKED,
                ProductUnlikedEvent(
                    (1..30).random().toLong(),
                ),
            )

            else -> Event(
                UUID.randomUUID().toString(),
                EventType.PRODUCT_VIEWED,
                ProductViewedEvent(
                    (1..30).random().toLong(),
                    "product:${(1..30).random()}",
                ),
            )
        }
    }
}
