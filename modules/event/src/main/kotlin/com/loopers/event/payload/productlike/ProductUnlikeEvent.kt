package com.loopers.event.payload.productlike

import com.loopers.event.payload.EventPayload

class ProductUnlikeEvent(
    val productId: Long,
) : EventPayload
