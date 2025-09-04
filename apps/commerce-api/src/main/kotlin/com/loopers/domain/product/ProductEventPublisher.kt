package com.loopers.domain.product

import com.loopers.event.payload.product.ProductChangedEvent
import com.loopers.event.payload.product.ProductViewedEvent

interface ProductEventPublisher {
    fun publish(productViewedEvent: ProductViewedEvent)

    fun publish(productChangedEvent: ProductChangedEvent)
}
