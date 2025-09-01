package com.loopers.event.payload.productlike

import com.loopers.event.payload.EventPayload

data class ProductUnlikeEvent(
    val productId: Long,
) : EventPayload
