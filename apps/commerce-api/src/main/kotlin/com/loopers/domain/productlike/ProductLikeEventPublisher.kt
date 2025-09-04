package com.loopers.domain.productlike

import com.loopers.event.payload.productlike.ProductLikedEvent
import com.loopers.event.payload.productlike.ProductUnlikedEvent

interface ProductLikeEventPublisher {
    fun publish(productLikedEvent: ProductLikedEvent)

    fun publish(productUnlikedEvent: ProductUnlikedEvent)
}
