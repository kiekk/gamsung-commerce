package com.loopers.infrastructure.productmetrics

import com.loopers.domain.productmetrics.ProductMetricsWeekly
import com.loopers.domain.productmetrics.ProductMetricsWeeklyRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ProductMetricsWeeklyRepositoryImpl(
    private val productMetricsWeeklyJpaRepository: ProductMetricsWeeklyJpaRepository,
) : ProductMetricsWeeklyRepository {
    override fun findTopByAggregateDateOrderByScoreDesc(
        aggregateDate: LocalDate,
        limit: Int,
    ): List<ProductMetricsWeekly> {
        return productMetricsWeeklyJpaRepository.findTopByAggregateDateOrderByScoreDesc(aggregateDate, limit)
    }

    override fun saveAll(productMetricsWeeklies: List<ProductMetricsWeekly>): List<ProductMetricsWeekly> {
        return productMetricsWeeklyJpaRepository.saveAll(productMetricsWeeklies)
    }
}
