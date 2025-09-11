package com.loopers.domain.productrank

interface ProductRankDailyRepository {
    fun saveAll(ranks: List<ProductRankDaily>)
}
