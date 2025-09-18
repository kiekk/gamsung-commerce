package com.loopers.domain.productmetrics

import java.time.LocalDate

interface ProductMetricsMonthlyRepository {
    fun findTopByAggregateDateOrderByScoreDesc(aggregateDate: LocalDate, limit: Int): List<ProductMetricsMonthly>

    fun saveAll(productMetricsMonthlies: List<ProductMetricsMonthly>): List<ProductMetricsMonthly>
}
