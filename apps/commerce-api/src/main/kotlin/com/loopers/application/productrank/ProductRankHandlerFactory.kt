package com.loopers.application.productrank

import org.springframework.stereotype.Component

@Component
class ProductRankHandlerFactory(
    private val productRankHandlers: List<ProductRankHandler>,
) {

    fun gerProductRanks(criteria: ProductRankHandlerCriteria.Search): List<ProductRankInfo.ProductRankList> {
        val handler = productRankHandlers.find { it.supports(criteria.rankType) }
            ?: throw IllegalArgumentException("지원하지 않는 랭킹 타입입니다: ${criteria.rankType}")

        return handler.getProductRanks(criteria)
    }

    fun getTotalCount(criteria: ProductRankHandlerCriteria.Search): Long {
        val handler = productRankHandlers.find { it.supports(criteria.rankType) }
            ?: throw IllegalArgumentException("지원하지 않는 랭킹 타입입니다: ${criteria.rankType}")

        return handler.getTotalCount(criteria.rankDate)
    }
}
