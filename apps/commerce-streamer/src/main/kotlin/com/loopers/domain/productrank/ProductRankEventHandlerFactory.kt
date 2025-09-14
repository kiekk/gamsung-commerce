package com.loopers.domain.productrank

import com.loopers.event.EventType
import com.loopers.event.payload.EventPayload
import com.loopers.support.cache.CacheKey
import org.springframework.stereotype.Component

@Component
class ProductRankEventHandlerFactory(
    private val handlers: List<ProductRankEventHandler<out EventPayload>>,
) {
    @Suppress("UNCHECKED_CAST")
    fun <T : EventPayload> handle(cacheKey: CacheKey, eventType: EventType, events: List<T>) {
        val handler = (
                handlers.find { it.supports(eventType) }
                        as? ProductRankEventHandler<T>
                    ?: throw IllegalStateException("해당 이벤트를 처리할 핸들러가 없습니다.")
                )
        handler.handle(cacheKey, events)
    }
}
