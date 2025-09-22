package com.loopers.application.productrank.handler

import com.loopers.application.productrank.ProductRankHandler
import com.loopers.application.productrank.ProductRankHandlerCriteria
import com.loopers.application.productrank.ProductRankInfo
import com.loopers.domain.productrank.view.MvProductRankMonthlyViewRepository
import com.loopers.support.enums.rank.RankType
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MonthlyProductRankHandler(
    private val mvProductRankMonthlyViewRepository: MvProductRankMonthlyViewRepository,
) : ProductRankHandler {
    override fun getProductRanks(criteria: ProductRankHandlerCriteria.Search): List<ProductRankInfo.ProductRankList> {
        return mvProductRankMonthlyViewRepository.getProductRanksByAggregateDate(
            criteria.rankDate.toLocalDate(),
            criteria.offset,
            criteria.limit,
        ).map { ProductRankInfo.ProductRankList.from(it) }
    }

    override fun getTotalCount(aggregateDate: LocalDateTime): Long {
        return mvProductRankMonthlyViewRepository.getTotalCountByAggregateDate(aggregateDate.toLocalDate())
    }

    override fun supports(type: RankType): Boolean {
        return type == RankType.MONTHLY
    }
}
