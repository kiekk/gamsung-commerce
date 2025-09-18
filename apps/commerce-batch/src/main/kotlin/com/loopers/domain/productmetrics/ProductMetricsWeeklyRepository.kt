package com.loopers.domain.productmetrics

import java.time.LocalDate

interface ProductMetricsWeeklyRepository {
    fun findTopByAggregateDateOrderByScoreDesc(aggregateDate: LocalDate, limit: Int): List<ProductMetricsWeekly>

    fun saveAll(productMetricsWeeklies: List<ProductMetricsWeekly>): List<ProductMetricsWeekly>
}
