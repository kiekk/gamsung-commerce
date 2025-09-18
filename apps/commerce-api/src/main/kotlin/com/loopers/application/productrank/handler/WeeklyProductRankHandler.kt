package com.loopers.application.productrank.handler

import com.loopers.application.productrank.ProductRankHandler
import com.loopers.application.productrank.ProductRankHandlerCriteria
import com.loopers.application.productrank.ProductRankInfo
import com.loopers.domain.productrank.view.MvProductRankWeeklyViewRepository
import com.loopers.support.enums.rank.RankType
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class WeeklyProductRankHandler(
    private val mvProductRankWeeklyViewRepository: MvProductRankWeeklyViewRepository,
) : ProductRankHandler {
    override fun getProductRanks(criteria: ProductRankHandlerCriteria.Search): List<ProductRankInfo.ProductRankList> {
        return mvProductRankWeeklyViewRepository.getProductRanksByAggregateDate(
            criteria.rankDate.toLocalDate(),
            criteria.offset,
            criteria.limit,
        ).map { ProductRankInfo.ProductRankList.from(it) }
    }

    override fun getTotalCount(aggregateDate: LocalDateTime): Long {
        return mvProductRankWeeklyViewRepository.getTotalCountByAggregateDate(aggregateDate.toLocalDate())
    }

    override fun supports(type: RankType): Boolean {
        return type == RankType.WEEKLY
    }
}
