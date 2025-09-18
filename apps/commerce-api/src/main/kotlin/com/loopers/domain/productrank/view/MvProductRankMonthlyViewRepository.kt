package com.loopers.domain.productrank.view

import java.time.LocalDate

interface MvProductRankMonthlyViewRepository {
    fun getProductRanksByAggregateDate(aggregateDate: LocalDate, offset: Long, limit: Int): List<MvProductRankMonthlyView>

    fun getTotalCountByAggregateDate(aggregateDate: LocalDate): Long
}
