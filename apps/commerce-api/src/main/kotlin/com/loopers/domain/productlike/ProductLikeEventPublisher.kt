package com.loopers.domain.productlike

import com.loopers.event.payload.productlike.ProductLikeEvent
import com.loopers.event.payload.productlike.ProductUnlikeEvent

interface ProductLikeEventPublisher {
    fun publish(productLikeEvent: ProductLikeEvent)

    fun publish(productUnlikeEvent: ProductUnlikeEvent)
}
