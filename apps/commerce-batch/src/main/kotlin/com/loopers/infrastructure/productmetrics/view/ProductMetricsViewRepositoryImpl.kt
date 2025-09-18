package com.loopers.infrastructure.productmetrics.view

import com.loopers.domain.productmetrics.view.ProductMetricsView
import com.loopers.domain.productmetrics.view.ProductMetricsViewRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ProductMetricsViewRepositoryImpl(
    private val productMetricsViewJpaRepository: ProductMetricsViewJpaRepository,
) : ProductMetricsViewRepository {
    override fun findGroupedByProductInRangeOrderBySums(
        aggregateStartDate: LocalDate,
        aggregateEndDate: LocalDate,
        limit: Int,
    ): List<ProductMetricsView> {
        return productMetricsViewJpaRepository.findGroupedByProductInRangeOrderBySums(
            aggregateStartDate,
            aggregateEndDate,
            limit,
        )
    }
}
