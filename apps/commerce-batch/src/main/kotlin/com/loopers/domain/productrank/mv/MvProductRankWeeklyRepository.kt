package com.loopers.domain.productrank.mv

import java.time.LocalDate

interface MvProductRankWeeklyRepository {
    fun deleteByAggregateDate(aggregateDate: LocalDate)

    fun saveAll(mvProductRankWeeklies: List<MvProductRankWeekly>)
}
