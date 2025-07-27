package com.loopers.domain.stock

interface StockRepository {
    fun save(stockEntity: StockEntity): StockEntity
}
