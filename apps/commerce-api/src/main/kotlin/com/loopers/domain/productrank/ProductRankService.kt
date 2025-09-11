package com.loopers.domain.productrank

import com.loopers.support.cache.CacheRepository
import com.loopers.support.cache.dto.ScoreRankDto
import com.loopers.support.cache.productrank.ProductRankCacheKeyGenerator
import org.springframework.stereotype.Service
import java.time.LocalDate

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
}
