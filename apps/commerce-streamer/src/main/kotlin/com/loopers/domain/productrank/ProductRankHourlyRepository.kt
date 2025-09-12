package com.loopers.domain.productrank

interface ProductRankHourlyRepository {
    fun saveAll(ranks: List<ProductRankHourly>)
}
