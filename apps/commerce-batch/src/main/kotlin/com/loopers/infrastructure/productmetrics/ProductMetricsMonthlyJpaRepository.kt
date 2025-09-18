package com.loopers.infrastructure.productmetrics

import com.loopers.domain.productmetrics.ProductMetricsMonthly
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface ProductMetricsMonthlyJpaRepository : JpaRepository<ProductMetricsMonthly, Long> {
    @Query(
        value = """
            SELECT pm
            FROM ProductMetricsMonthly pm
            WHERE pm.aggregateDate = :aggregateDate
            ORDER BY pm.score DESC
            LIMIT :limit
        """,
    )
    fun findTopByAggregateDateOrderByScoreDesc(
        @Param("aggregateDate") aggregateDate: LocalDate,
        @Param("limit") limit: Int,
    ): List<ProductMetricsMonthly>
}
