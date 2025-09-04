package com.loopers.domain.catalog

import com.loopers.event.Event
import com.loopers.event.payload.EventPayload
import org.springframework.stereotype.Service

@Service
class CatalogService(
    private val catalogEventHandlerFactory: CatalogEventHandlerFactory,
) {
    fun handleEvent(event: Event<EventPayload>) {
        catalogEventHandlerFactory.handle(event)
    }
}
