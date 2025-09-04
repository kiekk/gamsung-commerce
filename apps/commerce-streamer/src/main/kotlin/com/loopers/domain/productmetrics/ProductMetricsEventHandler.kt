package com.loopers.domain.productmetrics

import com.loopers.event.EventType
import com.loopers.event.payload.EventPayload

interface ProductMetricsEventHandler<T : EventPayload> {
    fun handle(eventPayload: T)

    fun supports(eventType: EventType): Boolean
}
