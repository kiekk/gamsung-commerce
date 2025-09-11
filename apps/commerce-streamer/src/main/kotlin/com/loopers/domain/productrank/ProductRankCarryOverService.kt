package com.loopers.domain.productrank

import com.loopers.support.cache.CacheRepository
import com.loopers.support.cache.productrank.ProductRankCacheKeyGenerator
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.DefaultTypedTuple
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class ProductRankCarryOverService(
    private val cacheRepository: CacheRepository,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun carryOverTomorrowRank(topN: Int, normalize: Double) {
        log.info("[ProductRankCarryOverService.carryOverTomorrowRank] topN: {}, normalize: {}", topN, normalize)
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        val productRanksWithScore = cacheRepository.findTopRankByScoreDesc(ProductRankCacheKeyGenerator.generate(today), 0, topN)

        val normalizedTuples = productRanksWithScore.map { (productId, score) ->
            DefaultTypedTuple(productId, score * normalize)
        }.toSet()

        if (normalizedTuples.isEmpty()) {
            log.info("[ProductRankCarryOverService.carryOverTomorrowRank] 상품 랭킹이 비어있습니다. 오늘 날짜: {}", today)
            return
        }

        cacheRepository.zAddAll(ProductRankCacheKeyGenerator.generate(tomorrow), normalizedTuples)
    }

    fun carryOverNextHourRank(topN: Int, normalize: Double) {
        log.info("[ProductRankCarryOverService.carryOverNextHourRank] topN: {}, normalize: {}", topN, normalize)
        val now = LocalDateTime.now()
        val nextHour = now.plusHours(1)

        val productRanksWithScore = cacheRepository.findTopRankByScoreDesc(ProductRankCacheKeyGenerator.generate(now), 0, topN)

        val normalizedTuples = productRanksWithScore.map { (productId, score) ->
            DefaultTypedTuple(productId, score * normalize)
        }.toSet()

        if (normalizedTuples.isEmpty()) {
            log.info("[ProductRankCarryOverService.carryOverNextHourRank] 상품 랭킹이 비어있습니다. 현재 시간: {}", now)
            return
        }

        cacheRepository.zAddAll(ProductRankCacheKeyGenerator.generate(nextHour), normalizedTuples)
    }
}
