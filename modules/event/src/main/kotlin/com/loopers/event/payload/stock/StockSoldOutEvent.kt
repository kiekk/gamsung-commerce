package com.loopers.event.payload.stock

import com.loopers.event.payload.EventPayload

class StockSoldOutEvent(
    val productId: Long,
) : EventPayload
