package com.loopers.domain.productrank

import com.loopers.event.EventType
import com.loopers.event.payload.EventPayload
import com.loopers.support.cache.CacheKey
import com.loopers.support.cache.CacheRepository
import com.loopers.support.cache.productrank.ProductRankCacheKeyGenerator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class ProductRankService(
    private val productRankEventHandlerFactory: ProductRankEventHandlerFactory,
    private val cacheRepository: CacheRepository,
    private val productRankDailyRepository: ProductRankDailyRepository,
    private val productRankHourlyRepository: ProductRankHourlyRepository,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun handleEvent(cacheKey: CacheKey, eventType: EventType, events: List<EventPayload>) {
        productRankEventHandlerFactory.handle(cacheKey, eventType, events)
    }

    fun backupYesterdayProductRank(prevDate: LocalDate) {
        log.info("[ProductRankService.backupYesterdayProductRank] prevDate: $prevDate")
        val cacheKey = ProductRankCacheKeyGenerator.generate(prevDate)
        val yesterdayProductRanks = cacheRepository.findTopRankByScoreDesc(
            cacheKey,
            0,
            100,
        )

        val productRankDailies = mutableListOf<ProductRankDaily>()
        yesterdayProductRanks.onEachIndexed { index, productRank ->
            productRankDailies.add(
                ProductRankDaily(
                    productRank.key,
                    cacheKey.key,
                    index + 1,
                    productRank.value,
                ),
            )
        }

        productRankDailyRepository.saveAll(productRankDailies)
    }

    fun backupPrevHourProductRank(prevHours: LocalDateTime) {
        log.info("[ProductRankService.backupPrevHourProductRank] prevHours: $prevHours")
        val cacheKey = ProductRankCacheKeyGenerator.generate(prevHours)
        val prevHourProductRanks = cacheRepository.findTopRankByScoreDesc(
            cacheKey,
            0,
            100,
        )

        val productRankHourlies = mutableListOf<ProductRankHourly>()
        prevHourProductRanks.onEachIndexed { index, productRank ->
            productRankHourlies.add(
                ProductRankHourly(
                    productRank.key,
                    cacheKey.key,
                    index + 1,
                    productRank.value,
                ),
            )
        }

        productRankHourlyRepository.saveAll(productRankHourlies)
    }
}
