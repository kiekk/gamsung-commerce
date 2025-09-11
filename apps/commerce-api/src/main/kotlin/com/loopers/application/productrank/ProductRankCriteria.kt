package com.loopers.application.productrank

import com.loopers.domain.productrank.ProductRankCommand
import java.time.LocalDate

class ProductRankCriteria {
    data class SearchDay(
        val rankDate: LocalDate,
    ) {
        fun toCommand(offset: Long, limit: Int): ProductRankCommand.SearchDay {
            return ProductRankCommand.SearchDay(this.rankDate, offset, limit)
        }
    }
}
