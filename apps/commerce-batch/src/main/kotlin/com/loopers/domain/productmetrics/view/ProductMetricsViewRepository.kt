package com.loopers.domain.productmetrics.view

import java.time.LocalDate

interface ProductMetricsViewRepository {
    fun findGroupedByProductInRangeOrderBySums(
        aggregateStartDate: LocalDate,
        aggregateEndDate: LocalDate,
        limit: Int,
    ): List<ProductMetricsView>
}
