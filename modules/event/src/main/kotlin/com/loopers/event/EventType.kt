package com.loopers.event

import com.loopers.event.payload.EventPayload
import com.loopers.event.payload.product.ProductChangedEvent
import com.loopers.event.payload.product.ProductViewedEvent
import com.loopers.event.payload.productlike.ProductLikedEvent
import com.loopers.event.payload.productlike.ProductUnlikedEvent
import com.loopers.event.payload.stock.StockAdjustedEvent
import com.loopers.event.payload.stock.StockSoldOutEvent

enum class EventType(
    val payloadClass: Class<out EventPayload>,
    val topic: String,
) {
    PRODUCT_CHANGED(ProductChangedEvent::class.java, Topic.PRODUCT_V1_CHANGED),
    PRODUCT_STOCK_ADJUSTED(StockAdjustedEvent::class.java, Topic.PRODUCT_V1_STOCK_ADJUSTED),
    PRODUCT_STOCK_SOLD_OUT(StockSoldOutEvent::class.java, Topic.PRODUCT_V1_STOCK_SOLD_OUT),
    PRODUCT_LIKED(ProductLikedEvent::class.java, Topic.PRODUCT_V1_LIKE_CHANGED),
    PRODUCT_UNLIKED(ProductUnlikedEvent::class.java, Topic.PRODUCT_V1_LIKE_CHANGED),
    PRODUCT_VIEWED(ProductViewedEvent::class.java, Topic.PRODUCT_V1_VIEWED),
    ;

    class Topic {
        companion object {
            const val PRODUCT_V1_CHANGED = "product.v1.changed"
            const val PRODUCT_V1_STOCK_ADJUSTED = "product.v1.stock-adjusted"
            const val PRODUCT_V1_STOCK_SOLD_OUT = "product.v1.stock-sold-out"
            const val PRODUCT_V1_LIKE_CHANGED = "product.v1.like-changed"
            const val PRODUCT_V1_VIEWED = "product.v1.viewed"

            // DLT
            const val PRODUCT_V1_CHANGED_DLT = "product.v1.changed.dlt"
            const val PRODUCT_V1_STOCK_ADJUSTED_DLT = "product.v1.stock-adjusted.dlt"
            const val PRODUCT_V1_STOCK_SOLD_OUT_DLT = "product.v1.stock-sold-out.dlt"
            const val PRODUCT_V1_LIKE_CHANGED_DLT = "product.v1.like-changed.dlt"
            const val PRODUCT_V1_VIEWED_DLT = "product.v1.viewed.dlt"
        }
    }

    class Group {
        companion object {
            const val CATALOG_EVENTS = "catalog-events-consumer"
            const val METRICS_EVENTS = "metrics-events-consumer"
            const val AUDIT_LOG_EVENTS = "audit-log-events-consumer"

            // DLT
            const val DLT_EVENTS = "dlt-events-consumer"
        }
    }
}
