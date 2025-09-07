package com.loopers.domain.catalog

import com.loopers.event.Event
import com.loopers.event.payload.EventPayload
import org.springframework.stereotype.Component

@Component
class CatalogEventHandlerFactory(
    private val handlers: List<CatalogEventHandler<out EventPayload>>,
) {
    @Suppress("UNCHECKED_CAST")
    fun <T : EventPayload> handle(event: Event<T>) {
        val handler = handlers.find { it.supports(event.eventType) }
                as? CatalogEventHandler<T>
            ?: throw IllegalStateException("해당 이벤트를 처리할 핸들러가 없습니다.")
        handler.handle(event.payload)
    }
}
