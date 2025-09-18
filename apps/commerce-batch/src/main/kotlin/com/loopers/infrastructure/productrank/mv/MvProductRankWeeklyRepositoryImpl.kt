package com.loopers.infrastructure.productrank.mv

import com.loopers.domain.productrank.mv.MvProductRankWeekly
import com.loopers.domain.productrank.mv.MvProductRankWeeklyRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class MvProductRankWeeklyRepositoryImpl(
    private val mvProductRankWeeklyJpaRepository: MvProductRankWeeklyJpaRepository,
) : MvProductRankWeeklyRepository {
    override fun deleteByAggregateDate(aggregateDate: LocalDate) {
        mvProductRankWeeklyJpaRepository.deleteByAggregateDate(aggregateDate)
    }

    override fun saveAll(mvProductRankWeeklies: List<MvProductRankWeekly>) {
        mvProductRankWeeklyJpaRepository.saveAll(mvProductRankWeeklies)
    }
}
