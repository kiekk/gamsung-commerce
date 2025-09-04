package com.loopers.domain.stock

import com.loopers.event.payload.stock.StockAdjustedEvent
import com.loopers.event.payload.stock.StockSoldOutEvent

interface StockEventPublisher {
    fun publish(stockSoldOutEvent: StockSoldOutEvent)

    fun publish(stockAdjustedEvent: StockAdjustedEvent)
}
