package com.loopers.infrastructure.productrank.view

import com.loopers.domain.productrank.view.MvProductRankWeeklyView
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface MvProductRankWeeklyViewJpaRepository : JpaRepository<MvProductRankWeeklyView, Long> {
    @Query(
        """
        SELECT mprwv
        FROM MvProductRankWeeklyView mprwv
        WHERE mprwv.aggregateDate = :aggregateDate
        ORDER BY mprwv.rankNumber ASC
        LIMIT :limit OFFSET :offset
        """,
    )
    fun findByAggregateDate(aggregateDate: LocalDate, offset: Long, limit: Int): List<MvProductRankWeeklyView>

    @Query(
        """
        SELECT COUNT(*) AS total_count
        FROM mv_product_rank_weekly 
        WHERE aggregate_date = :aggregateDate
        """,
        nativeQuery = true,
    )
    fun countByAggregateDate(aggregateDate: LocalDate): Long
}
