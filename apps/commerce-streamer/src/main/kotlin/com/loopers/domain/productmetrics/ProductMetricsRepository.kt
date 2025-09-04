package com.loopers.domain.productmetrics

import java.time.LocalDate

interface ProductMetricsRepository {
    fun findByProductIdAndMetricDate(productId: Long, metricDate: LocalDate): ProductMetrics?

    fun save(productMetrics: ProductMetrics)
}
