package com.loopers.application.productrank

import com.loopers.domain.productrank.ProductRankCommand
import com.loopers.support.enums.rank.RankType
import java.time.LocalDate
import java.time.LocalDateTime

class ProductRankCriteria {
    data class Search(
        val rankDate: LocalDateTime,
        val rankType: RankType,
    ) {
        fun toProductRankHandlerCriteria(offset: Long, limit: Int): ProductRankHandlerCriteria.Search {
            return ProductRankHandlerCriteria.Search(rankDate, rankType, offset, limit)
        }
    }

    data class SearchDay(
        val rankDate: LocalDate,
    ) {
        fun toCommand(offset: Long, limit: Int): ProductRankCommand.SearchDay {
            return ProductRankCommand.SearchDay(rankDate, offset, limit)
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
