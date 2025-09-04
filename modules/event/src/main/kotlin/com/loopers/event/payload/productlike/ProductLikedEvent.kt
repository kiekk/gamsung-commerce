package com.loopers.event.payload.productlike

import com.loopers.event.payload.EventPayload

data class ProductLikedEvent(
    val productId: Long,
) : EventPayload
