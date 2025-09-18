package com.loopers.infrastructure.productrank.mv

import com.loopers.domain.productrank.mv.MvProductRankWeekly
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface MvProductRankWeeklyJpaRepository : JpaRepository<MvProductRankWeekly, Long> {
    fun deleteByAggregateDate(aggregateDate: LocalDate)
}
