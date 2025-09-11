package com.loopers.domain.productmetrics.handler

import com.loopers.domain.productmetrics.ProductMetrics
import com.loopers.domain.productmetrics.ProductMetricsEventHandler
import com.loopers.domain.productmetrics.ProductMetricsRepository
import com.loopers.event.EventType
import com.loopers.event.payload.productlike.ProductUnlikedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class ProductMetricsProductUnlikedEventHandlerProduct(
    private val productMetricsRepository: ProductMetricsRepository,
) : ProductMetricsEventHandler<ProductUnlikedEvent> {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun handle(eventPayload: ProductUnlikedEvent) {
        log.info("[ProductUnlikedEventHandlerProduct.handle] eventPayload: $eventPayload")
        val productMetrics = productMetricsRepository.findByProductIdAndMetricDate(eventPayload.productId, LocalDate.now())
            ?: ProductMetrics.init(eventPayload.productId, LocalDate.now())
        productMetrics.decreaseLikeCount()
        productMetricsRepository.save(productMetrics)
    }

    override fun supports(eventType: EventType): Boolean {
        return EventType.PRODUCT_UNLIKED == eventType
    }
}
