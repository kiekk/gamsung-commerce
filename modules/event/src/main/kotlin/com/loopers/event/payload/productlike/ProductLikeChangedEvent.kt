package com.loopers.event.payload.productlike

import com.loopers.event.payload.EventPayload

data class ProductLikeChangedEvent(
    val productId: Long,
) : EventPayload
