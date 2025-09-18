package com.loopers.application.productrank.handler

import com.loopers.application.productrank.ProductRankHandler
import com.loopers.application.productrank.ProductRankHandlerCriteria
import com.loopers.application.productrank.ProductRankInfo
import com.loopers.domain.product.query.ProductQueryService
import com.loopers.support.cache.CacheRepository
import com.loopers.support.cache.productrank.ProductRankCacheKeyGenerator
import com.loopers.support.enums.rank.RankType
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class HourlyProductRankHandler(
    private val productQueryService: ProductQueryService,
    private val cacheRepository: CacheRepository,
) : ProductRankHandler {
    override fun getProductRanks(criteria: ProductRankHandlerCriteria.Search): List<ProductRankInfo.ProductRankList> {
        val productRankMap = cacheRepository.findTopRankByScoreDesc(
            ProductRankCacheKeyGenerator.generate(criteria.rankDate),
            criteria.offset,
            criteria.limit,
        )
        val products = productQueryService.getProductsByIds(productRankMap.keys.map { it.toLong() }.toList())
            ?: emptyList()
        // 랭크 정보 추가
        return products
            .map {
                ProductRankInfo.ProductRankList.from(
                    it,
                    productRankMap[it.id.toString()]?.rank,
                    productRankMap[it.id.toString()]?.score,
                )
            }
            .sortedBy { it.rankNumber }
            .toList()
    }

    override fun getTotalCount(aggregateDate: LocalDateTime): Long {
        return cacheRepository.getTotalCount(
            ProductRankCacheKeyGenerator.generate(aggregateDate),
        )
    }

    override fun supports(type: RankType): Boolean {
        return type == RankType.HOURLY
    }
}
