package com.loopers.infrastructure.productrank.mv

import com.loopers.domain.productrank.mv.MvProductRankMonthly
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface MvProductRankMonthlyJpaRepository : JpaRepository<MvProductRankMonthly, Long> {
    fun deleteByAggregateDate(aggregateDate: LocalDate)
}
