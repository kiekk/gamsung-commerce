package com.loopers.domain.productmetrics

import com.loopers.event.Event
import com.loopers.event.payload.EventPayload
import org.springframework.stereotype.Service

@Service
class ProductMetricsService(
    private val eventHandlerFactory: ProductMetricsEventHandlerFactory,
) {
    fun handleEvent(event: Event<out EventPayload>) {
        eventHandlerFactory.handle(event)
    }
}
