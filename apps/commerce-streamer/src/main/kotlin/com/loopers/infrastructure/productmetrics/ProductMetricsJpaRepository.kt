package com.loopers.infrastructure.productmetrics

import com.loopers.domain.productmetrics.ProductMetrics
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ProductMetricsJpaRepository : JpaRepository<ProductMetrics, Long> {
    fun findByProductIdAndMetricDate(productId: Long, metricDate: LocalDate): ProductMetrics?
}
