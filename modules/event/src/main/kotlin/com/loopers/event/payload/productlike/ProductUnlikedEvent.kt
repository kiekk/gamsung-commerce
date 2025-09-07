package com.loopers.event.payload.productlike

import com.loopers.event.payload.EventPayload

data class ProductUnlikedEvent(
    val productId: Long,
) : EventPayload
