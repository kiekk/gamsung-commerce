package com.loopers.application.productrank

import com.loopers.support.enums.rank.RankType
import java.time.LocalDateTime

class ProductRankHandlerCriteria {
    data class Search(
        val rankDate: LocalDateTime,
        val rankType: RankType,
        val offset: Long,
        val limit: Int,
    )
}
