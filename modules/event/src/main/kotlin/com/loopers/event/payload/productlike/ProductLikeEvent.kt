package com.loopers.event.payload.productlike

import com.loopers.event.payload.EventPayload

data class ProductLikeEvent(
    val productId: Long,
) : EventPayload
