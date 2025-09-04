package com.loopers.domain.catalog

import com.loopers.event.EventType
import com.loopers.event.payload.EventPayload

interface CatalogEventHandler<T : EventPayload> {
    fun handle(eventPayload: T)

    fun supports(eventType: EventType): Boolean
}
