package com.loopers.infrastructure.stock

import com.loopers.domain.stock.StockEntity
import com.loopers.domain.stock.StockRepository
import org.springframework.stereotype.Repository

@Repository
class StockRepositoryImpl(
    private val stockJpaRepository: StockJpaRepository,
) : StockRepository {
    override fun save(stockEntity: StockEntity): StockEntity {
        return stockJpaRepository.save(stockEntity)
    }
}
