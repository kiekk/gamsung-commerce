package com.loopers.event.payload.product

import com.loopers.event.payload.EventPayload

data class ProductChangedEvent(
    val productId: Long,
) : EventPayload
