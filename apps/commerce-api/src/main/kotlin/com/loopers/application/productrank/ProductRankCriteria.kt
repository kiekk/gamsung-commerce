package com.loopers.application.productrank

import com.loopers.domain.productrank.ProductRankCommand
import java.time.LocalDate
import java.time.LocalDateTime

class ProductRankCriteria {
    data class SearchDay(
        val rankDate: LocalDate,
    ) {
        fun toCommand(offset: Long, limit: Int): ProductRankCommand.SearchDay {
            return ProductRankCommand.SearchDay(this.rankDate, offset, limit)
        }
    }

    data class SearchHour(
        val rankDate: LocalDateTime,
    ) {
        fun toCommand(offset: Long, limit: Int): ProductRankCommand.SearchHour {
            return ProductRankCommand.SearchHour(rankDate, offset, limit)
        }
    }
}
