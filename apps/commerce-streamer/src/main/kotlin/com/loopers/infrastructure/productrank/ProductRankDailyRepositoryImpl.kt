package com.loopers.infrastructure.productrank

import com.loopers.domain.productrank.ProductRankDaily
import com.loopers.domain.productrank.ProductRankDailyRepository
import org.springframework.stereotype.Repository

@Repository
class ProductRankDailyRepositoryImpl(
    private val productRankDailyJpaRepository: ProductRankDailyJpaRepository,
) : ProductRankDailyRepository {
    override fun saveAll(ranks: List<ProductRankDaily>) {
        productRankDailyJpaRepository.saveAll(ranks)
    }
}
