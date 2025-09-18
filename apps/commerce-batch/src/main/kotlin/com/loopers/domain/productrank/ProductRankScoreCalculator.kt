package com.loopers.domain.productrank

object ProductRankScoreCalculator {
    fun calculate(likeCount: Int, viewCount: Int, salesCount: Int): Double {
        val likeScore = likeCount * ProductRankScoreWeight.PRODUCT_LIKE_COUNT_WEIGHT
        val viewScore = viewCount * ProductRankScoreWeight.PRODUCT_VIEW_COUNT_WEIGHT
        val salesScore = salesCount * ProductRankScoreWeight.PRODUCT_SALES_COUNT_WEIGHT
        return likeScore + viewScore + salesScore
    }
}
