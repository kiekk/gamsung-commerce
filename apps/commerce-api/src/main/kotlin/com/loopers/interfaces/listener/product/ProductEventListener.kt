package com.loopers.interfaces.listener.product

import com.loopers.event.payload.product.ProductViewedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ProductEventListener {

    private val log = LoggerFactory.getLogger(this::class.java)

    @EventListener
    fun handle(event: ProductViewedEvent) {
        log.info("상품 조회 이벤트 수신: $event")
    }
}
