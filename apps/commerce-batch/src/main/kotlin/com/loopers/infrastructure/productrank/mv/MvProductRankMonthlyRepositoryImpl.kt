package com.loopers.infrastructure.productrank.mv

import com.loopers.domain.productrank.mv.MvProductRankMonthly
import com.loopers.domain.productrank.mv.MvProductRankMonthlyRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class MvProductRankMonthlyRepositoryImpl(
    private val mvProductRankMonthlyJpaRepository: MvProductRankMonthlyJpaRepository,
) : MvProductRankMonthlyRepository {
    override fun deleteByAggregateDate(aggregateDate: LocalDate) {
        mvProductRankMonthlyJpaRepository.deleteByAggregateDate(aggregateDate)
    }

    override fun saveAll(mvProductRankMonthlies: List<MvProductRankMonthly>) {
        mvProductRankMonthlyJpaRepository.saveAll(mvProductRankMonthlies)
    }
}
