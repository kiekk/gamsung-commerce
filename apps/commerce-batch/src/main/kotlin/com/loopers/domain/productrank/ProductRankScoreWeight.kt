package com.loopers.domain.productrank

object ProductRankScoreWeight {
    const val PRODUCT_VIEW_COUNT_WEIGHT: Double = 0.1 // 조회수 가중치
    const val PRODUCT_LIKE_COUNT_WEIGHT: Double = 0.3 // 좋아요수 가중치
    const val PRODUCT_SALES_COUNT_WEIGHT: Double = 0.7 // 판매량수 가중치
}
