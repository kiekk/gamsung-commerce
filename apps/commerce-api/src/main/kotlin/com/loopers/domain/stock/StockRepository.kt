package com.loopers.domain.stock

interface StockRepository {
    fun save(stockEntity: StockEntity): StockEntity

    fun findAllByProductIdsWithLock(productIds: List<Long>): List<StockEntity>

    fun findByProductId(productId: Long): StockEntity?
}
