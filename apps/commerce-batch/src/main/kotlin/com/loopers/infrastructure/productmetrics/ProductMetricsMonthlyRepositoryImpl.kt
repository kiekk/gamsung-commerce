package com.loopers.infrastructure.productmetrics

import com.loopers.domain.productmetrics.ProductMetricsMonthly
import com.loopers.domain.productmetrics.ProductMetricsMonthlyRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ProductMetricsMonthlyRepositoryImpl(
    private val productMetricsMonthlyJpaRepository: ProductMetricsMonthlyJpaRepository,
) : ProductMetricsMonthlyRepository {
    override fun findTopByAggregateDateOrderByScoreDesc(
        aggregateDate: LocalDate,
        limit: Int,
    ): List<ProductMetricsMonthly> {
        return productMetricsMonthlyJpaRepository.findTopByAggregateDateOrderByScoreDesc(aggregateDate, limit)
    }

    override fun saveAll(productMetricsMonthlies: List<ProductMetricsMonthly>): List<ProductMetricsMonthly> {
        return productMetricsMonthlyJpaRepository.saveAll(productMetricsMonthlies)
    }
}
