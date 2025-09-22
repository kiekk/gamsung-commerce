package com.loopers.infrastructure.productrank.view

import com.loopers.domain.productrank.view.MvProductRankWeeklyView
import com.loopers.domain.productrank.view.MvProductRankWeeklyViewRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class MvProductRankWeeklyViewRepositoryImpl(
    private val mvProductRankWeeklyViewJpaRepository: MvProductRankWeeklyViewJpaRepository,
) : MvProductRankWeeklyViewRepository {
    override fun getProductRanksByAggregateDate(
        aggregateDate: LocalDate,
        offset: Long,
        limit: Int,
    ): List<MvProductRankWeeklyView> {
        return mvProductRankWeeklyViewJpaRepository.findByAggregateDate(aggregateDate, offset, limit)
    }

    override fun getTotalCountByAggregateDate(aggregateDate: LocalDate): Long {
        return mvProductRankWeeklyViewJpaRepository.countByAggregateDate(aggregateDate)
    }
}
