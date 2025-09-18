package com.loopers.infrastructure.productrank.view

import com.loopers.domain.productrank.view.MvProductRankMonthlyView
import com.loopers.domain.productrank.view.MvProductRankMonthlyViewRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class MvProductRankMonthlyViewRepositoryImpl(
    private val mvProductRankMonthlyViewJpaRepository: MvProductRankMonthlyViewJpaRepository,
) : MvProductRankMonthlyViewRepository {
    override fun getProductRanksByAggregateDate(
        aggregateDate: LocalDate,
        offset: Long,
        limit: Int,
    ): List<MvProductRankMonthlyView> {
        return mvProductRankMonthlyViewJpaRepository.findByAggregateDate(aggregateDate, offset, limit)
    }

    override fun getTotalCountByAggregateDate(aggregateDate: LocalDate): Long {
        return mvProductRankMonthlyViewJpaRepository.countByAggregateDate(aggregateDate)
    }
}
