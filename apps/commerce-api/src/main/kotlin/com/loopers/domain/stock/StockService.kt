package com.loopers.domain.stock

import com.loopers.domain.product.ProductEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockService(
    private val stockRepository: StockRepository,
) {
    @Transactional
    fun createStock(command: StockCommand.Create): StockEntity {
        return stockRepository.save(command.toEntity())
    }

    @Transactional(readOnly = true)
    fun getStocksByProductIds(productIds: List<Long>): List<StockEntity> {
        return stockRepository.findByIds(productIds)
    }
}
