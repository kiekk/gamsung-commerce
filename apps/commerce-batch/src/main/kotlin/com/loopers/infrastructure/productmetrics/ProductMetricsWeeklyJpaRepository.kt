package com.loopers.infrastructure.productmetrics

import com.loopers.domain.productmetrics.ProductMetricsWeekly
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface ProductMetricsWeeklyJpaRepository : JpaRepository<ProductMetricsWeekly, Long> {
    @Query(
        value = """
            SELECT pw
            FROM ProductMetricsWeekly pw
            WHERE pw.aggregateDate = :aggregateDate
            ORDER BY pw.score DESC
            LIMIT :limit
        """,
    )
    fun findTopByAggregateDateOrderByScoreDesc(
        @Param("aggregateDate") aggregateDate: LocalDate,
        @Param("limit") limit: Int,
    ): List<ProductMetricsWeekly>
}
