package com.loopers.domain.productrank

import com.loopers.event.EventType
import com.loopers.event.payload.EventPayload
import com.loopers.support.cache.CacheKey

interface ProductRankEventHandler<T : EventPayload> {
    fun handle(cacheKey: CacheKey, events: List<T>)

    fun supports(eventType: EventType): Boolean
}
