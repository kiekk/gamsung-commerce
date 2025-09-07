package com.loopers.infrastructure.productmetrics

import com.loopers.domain.productmetrics.ProductMetrics
import com.loopers.domain.productmetrics.ProductMetricsRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ProductMetricsRepositoryImpl(
    private val productMetricsJpaRepository: ProductMetricsJpaRepository,
) : ProductMetricsRepository {
    override fun findByProductIdAndMetricDate(productId: Long, metricDate: LocalDate): ProductMetrics? {
        return productMetricsJpaRepository.findByProductIdAndMetricDate(productId, metricDate)
    }

    override fun save(productMetrics: ProductMetrics) {
        productMetricsJpaRepository.save(productMetrics)
    }
}
