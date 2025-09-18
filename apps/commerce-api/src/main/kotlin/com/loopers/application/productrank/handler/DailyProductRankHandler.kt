package com.loopers.application.productrank.handler

import com.loopers.application.productrank.ProductRankHandler
import com.loopers.application.productrank.ProductRankHandlerCriteria
import com.loopers.application.productrank.ProductRankInfo
import com.loopers.support.enums.rank.RankType
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DailyProductRankHandler : ProductRankHandler {
    override fun getProductRanks(criteria: ProductRankHandlerCriteria.Search): List<ProductRankInfo.ProductRankList> {
        TODO("Not yet implemented")
    }

    override fun getTotalCount(aggregateDate: LocalDateTime): Long {
        TODO("Not yet implemented")
    }

    override fun supports(type: RankType): Boolean {
        return type == RankType.DAILY
    }
}
