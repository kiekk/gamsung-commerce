package com.loopers.domain.productrank

import com.loopers.event.Event
import com.loopers.event.payload.EventPayload
import com.loopers.support.cache.CacheKey
import org.springframework.stereotype.Component

@Component
class ProductRankEventHandlerFactory(
    private val handlers: List<ProductRankEventHandler<out EventPayload>>,
) {
    @Suppress("UNCHECKED_CAST")
    fun <T : EventPayload> handle(cacheKey: CacheKey, event: Event<T>) {
        val handler = (
            handlers.find { it.supports(event.eventType) }
                as? ProductRankEventHandler<T>
            ?: throw IllegalStateException("해당 이벤트를 처리할 핸들러가 없습니다.")
        )
        handler.handle(cacheKey, event.payload)
    }
}
