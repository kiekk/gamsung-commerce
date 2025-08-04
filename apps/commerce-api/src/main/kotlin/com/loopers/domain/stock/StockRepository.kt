package com.loopers.domain.stock

interface StockRepository {
    fun save(stockEntity: StockEntity): StockEntity

    fun findByIds(productIds: List<Long>): List<StockEntity>
}
