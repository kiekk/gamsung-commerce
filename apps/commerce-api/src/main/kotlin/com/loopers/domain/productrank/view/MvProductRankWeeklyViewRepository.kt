package com.loopers.domain.productrank.view

import java.time.LocalDate

interface MvProductRankWeeklyViewRepository {
    fun getProductRanksByAggregateDate(aggregateDate: LocalDate, offset: Long, limit: Int): List<MvProductRankWeeklyView>

    fun getTotalCountByAggregateDate(aggregateDate: LocalDate): Long
}
