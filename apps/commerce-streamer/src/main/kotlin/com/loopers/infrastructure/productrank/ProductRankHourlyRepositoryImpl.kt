package com.loopers.infrastructure.productrank

import com.loopers.domain.productrank.ProductRankHourly
import com.loopers.domain.productrank.ProductRankHourlyRepository
import org.springframework.stereotype.Repository

@Repository
class ProductRankHourlyRepositoryImpl(
    private val productRankHourlyJpaRepository: ProductRankHourlyJpaRepository,
) : ProductRankHourlyRepository {
    override fun saveAll(ranks: List<ProductRankHourly>) {
        productRankHourlyJpaRepository.saveAll(ranks)
    }
}
