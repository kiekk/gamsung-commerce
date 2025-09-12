package com.loopers.domain.productrank

import com.loopers.support.cache.CacheRepository
import com.loopers.support.cache.dto.ScoreRankDto
import com.loopers.support.cache.productrank.ProductRankCacheKeyGenerator
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class ProductRankService(
    private val cacheRepository: CacheRepository,
) {

    fun getProductRankIdsByDay(command: ProductRankCommand.SearchDay): Map<String, ScoreRankDto> {
        return cacheRepository.findTopRankByScoreDesc(
            ProductRankCacheKeyGenerator.generate(command.rankDate),
            command.offset,
            command.limit,
        )
    }

    fun getProductRankTotalCountByDay(rankDate: LocalDate): Long {
        return cacheRepository.getTotalCount(ProductRankCacheKeyGenerator.generate(rankDate))
    }

    fun getProductRankByDay(productId: Long): Long? {
        return cacheRepository.findRank(
            ProductRankCacheKeyGenerator.generate(LocalDate.now()),
            productId.toString(),
        )
    }

    fun getProductRankIdsByHour(command: ProductRankCommand.SearchHour): Map<String, ScoreRankDto> {
        return cacheRepository.findTopRankByScoreDesc(
            ProductRankCacheKeyGenerator.generate(command.rankDate),
            command.offset,
            command.limit,
        )
    }

    fun getProductRankTotalCountByHour(rankDate: LocalDateTime): Long {
        return cacheRepository.getTotalCount(ProductRankCacheKeyGenerator.generate(rankDate))
    }
}
