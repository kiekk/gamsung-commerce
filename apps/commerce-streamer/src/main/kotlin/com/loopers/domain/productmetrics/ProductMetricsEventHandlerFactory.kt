package com.loopers.domain.productmetrics

import com.loopers.event.Event
import com.loopers.event.payload.EventPayload
import org.springframework.stereotype.Component

@Component
class ProductMetricsEventHandlerFactory(
    private val handlers: List<ProductMetricsEventHandler<EventPayload>>,
) {
    fun handle(event: Event<EventPayload>) {
        handlers.find { it.supports(event.eventType) }?.handle(event.payload)
            ?: throw IllegalStateException("해당 이벤트를 처리할 핸들러가 없습니다.")
    }
}
