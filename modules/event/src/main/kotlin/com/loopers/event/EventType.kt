package com.loopers.event

import com.loopers.event.payload.EventPayload
import com.loopers.event.payload.product.ProductChangedEvent
import com.loopers.event.payload.productlike.ProductLikeChangedEvent
import com.loopers.event.payload.stock.StockAdjustedEvent

enum class EventType(
    val payloadClass: Class<out EventPayload>,
    val topic: String,
) {
    PRODUCT_CHANGED(ProductChangedEvent::class.java, Topic.PRODUCT_CHANGED),
    PRODUCT_STOCK_ADJUSTED(StockAdjustedEvent::class.java, Topic.PRODUCT_STOCK_ADJUSTED),
    PRODUCT_LIKED(ProductLikeChangedEvent::class.java, Topic.PRODUCT_LIKE_CHANGED),
    PRODUCT_UNLIKED(ProductLikeChangedEvent::class.java, Topic.PRODUCT_LIKE_CHANGED),
    ;


    class Topic {
        companion object {
            const val PRODUCT_CHANGED = "product-changed"
            const val PRODUCT_STOCK_ADJUSTED = "product-stock-adjusted"
            const val PRODUCT_LIKE_CHANGED = "product-like-changed"
        }
    }

    class Group {
        companion object {
            const val CATALOG_EVENTS = "catalog-events-listener-group"
        }
    }
}
