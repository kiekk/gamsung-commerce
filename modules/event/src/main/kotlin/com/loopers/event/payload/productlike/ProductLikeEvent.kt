package com.loopers.event.payload.productlike

import com.loopers.event.payload.EventPayload

class ProductLikeEvent(
    val productId: Long,
) : EventPayload
