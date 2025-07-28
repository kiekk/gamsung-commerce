package com.loopers.domain.stock

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockService(
    private val stockRepository: StockRepository,
) {
    @Transactional
    fun createStock(stockEntity: StockEntity): StockEntity {
        return stockRepository.save(stockEntity)
    }
}
