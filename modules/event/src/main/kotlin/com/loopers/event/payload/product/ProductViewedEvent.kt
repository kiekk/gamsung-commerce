package com.loopers.event.payload.product

import com.loopers.event.payload.EventPayload

data class ProductViewedEvent(
    val productId: Long,
    val productName: String,
) : EventPayload
