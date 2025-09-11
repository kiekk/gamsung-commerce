package com.loopers.domain.productrank

import java.time.LocalDate

class ProductRankCommand {
    data class SearchDay(
        val rankDate: LocalDate,
        val offset: Long,
        val limit: Int,
    )
}
