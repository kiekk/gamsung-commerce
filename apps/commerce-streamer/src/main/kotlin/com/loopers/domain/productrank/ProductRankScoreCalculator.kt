package com.loopers.domain.productrank

import kotlin.math.log10

object ProductRankScoreCalculator {
    fun calculateScoreByViewCount(viewCount: Int): Double {
        return viewCount * ProductRankScoreWeight.PRODUCT_VIEW_COUNT_WEIGHT
    }

    fun calculateScoreByLikeCount(likeCount: Int): Double {
        return likeCount * ProductRankScoreWeight.PRODUCT_LIKE_COUNT_WEIGHT
    }

    fun calculateScoreBySalesCount(salesCount: Int, amount: Long): Double {
        return log10((salesCount * amount) * ProductRankScoreWeight.PRODUCT_SALES_COUNT_WEIGHT)
    }
}
