package com.loopers.event.payload.stock

import com.loopers.event.payload.EventPayload

data class StockAdjustedEvent(
    val productId: Long,
    val quantity: Int,
    val amount: Long,
) : EventPayload
