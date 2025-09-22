package com.loopers.application.productrank

import com.loopers.support.enums.rank.RankType
import java.time.LocalDateTime

interface ProductRankHandler {
    fun getProductRanks(criteria: ProductRankHandlerCriteria.Search): List<ProductRankInfo.ProductRankList>

    fun getTotalCount(aggregateDate: LocalDateTime): Long

    fun supports(type: RankType): Boolean
}
