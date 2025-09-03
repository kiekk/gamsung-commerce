package com.loopers.domain.product

import com.loopers.event.payload.product.ProductChangedEvent

interface ProductEventPublisher {
    fun publish(productChangedEvent: ProductChangedEvent)
}
