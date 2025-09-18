package com.loopers.infrastructure.productrank.view

import com.loopers.domain.productrank.view.MvProductRankMonthlyView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface MvProductRankMonthlyViewJpaRepository : JpaRepository<MvProductRankMonthlyView, Long> {
    @Query(
        """
        SELECT mprmv
        FROM MvProductRankMonthlyView mprmv
        WHERE mprmv.aggregateDate = :aggregateDate
        ORDER BY mprmv.rankNumber ASC
        LIMIT :limit OFFSET :offset
        """,
    )
    fun findByAggregateDate(aggregateDate: LocalDate, offset: Long, limit: Int): List<MvProductRankMonthlyView>

    @Query(
        """
        SELECT COUNT(*) AS total_count
        FROM mv_product_rank_monthly 
        WHERE aggregate_date = :aggregateDate
        """,
        nativeQuery = true,
    )
    fun countByAggregateDate(aggregateDate: LocalDate): Long
}
