package com.loopers.domain.productrank.mv

import java.time.LocalDate

interface MvProductRankMonthlyRepository {
    fun deleteByAggregateDate(aggregateDate: LocalDate)

    fun saveAll(mvProductRankMonthlies: List<MvProductRankMonthly>)
}
