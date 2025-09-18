package com.loopers.infrastructure.productmetrics.view

import com.loopers.domain.productmetrics.view.ProductMetricsView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface ProductMetricsViewJpaRepository : JpaRepository<ProductMetricsView, Long> {
    @Query(
        value = """
            SELECT 
                MAX(pm.id)                       AS id,
                pm.product_id                    AS product_id,
                MAX(pm.metric_date)              AS metric_date,
                COALESCE(SUM(pm.like_count), 0)  AS like_count,
                COALESCE(SUM(pm.view_count), 0)  AS view_count,
                COALESCE(SUM(pm.sales_count), 0) AS sales_count
            FROM product_metrics pm
            WHERE pm.metric_date >= :aggregateStartDate
              AND pm.metric_date <  :aggregateEndDate
            GROUP BY pm.product_id
            ORDER BY SUM(pm.sales_count) DESC, SUM(pm.view_count) DESC, SUM(pm.like_count) DESC
            LIMIT :limit
        """,
        nativeQuery = true,
    )
    fun findGroupedByProductInRangeOrderBySums(
        @Param("aggregateStartDate") aggregateStartDate: LocalDate,
        @Param("aggregateEndDate") aggregateEndDate: LocalDate,
        @Param("limit") limit: Int,
    ): List<ProductMetricsView>
}
