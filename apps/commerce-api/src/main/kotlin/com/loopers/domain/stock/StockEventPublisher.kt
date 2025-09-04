package com.loopers.domain.stock

import com.loopers.event.payload.stock.StockAdjustedEvent

interface StockEventPublisher {
    fun publish(stockAdjustedEvent: StockAdjustedEvent)
}
