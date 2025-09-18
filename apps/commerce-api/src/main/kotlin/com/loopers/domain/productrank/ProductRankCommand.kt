package com.loopers.domain.productrank

import com.loopers.support.enums.rank.RankType
import java.time.LocalDate
import java.time.LocalDateTime

class ProductRankCommand {
    data class Search(
        val rankDate: LocalDateTime,
        val rankType: RankType,
        val offset: Long,
        val limit: Int,
    )

    data class SearchDay(
        val rankDate: LocalDate,
        val offset: Long,
        val limit: Int,
    )

    data class SearchHour(
        val rankDate: LocalDateTime,
        val offset: Long,
        val limit: Int,
    )
}
