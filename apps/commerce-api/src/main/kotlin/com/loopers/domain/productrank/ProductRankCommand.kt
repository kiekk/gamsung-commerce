package com.loopers.domain.productrank

import java.time.LocalDate
import java.time.LocalDateTime

class ProductRankCommand {
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
