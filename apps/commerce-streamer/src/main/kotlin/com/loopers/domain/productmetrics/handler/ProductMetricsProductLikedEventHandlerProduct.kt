package com.loopers.domain.productmetrics.handler

import com.loopers.domain.productmetrics.ProductMetrics
import com.loopers.domain.productmetrics.ProductMetricsEventHandler
import com.loopers.domain.productmetrics.ProductMetricsRepository
import com.loopers.event.EventType
import com.loopers.event.payload.productlike.ProductLikedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ProductMetricsProductLikedEventHandlerProduct(
    private val productMetricsRepository: ProductMetricsRepository,
) : ProductMetricsEventHandler<ProductLikedEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(eventPayload: ProductLikedEvent) {
        log.info("[ProductLikedEventHandlerProduct.handle] eventPayload: $eventPayload")
        val productMetrics = productMetricsRepository.findByProductIdAndMetricDate(eventPayload.productId, LocalDate.now())
            ?: ProductMetrics.init(eventPayload.productId, LocalDate.now())
        productMetrics.increaseLikeCount()
        productMetricsRepository.save(productMetrics)
    }

    override fun supports(eventType: EventType): Boolean {
        return EventType.PRODUCT_LIKED == eventType
    }
}
