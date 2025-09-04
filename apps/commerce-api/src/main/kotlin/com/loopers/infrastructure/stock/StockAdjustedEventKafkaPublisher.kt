package com.loopers.infrastructure.stock

import com.loopers.domain.stock.StockEventPublisher
import com.loopers.event.Event
import com.loopers.event.EventType
import com.loopers.event.payload.stock.StockAdjustedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class StockAdjustedEventKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : StockEventPublisher {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun publish(stockAdjustedEvent: StockAdjustedEvent) {
        log.info("[StockAdjustedEventKafkaPublisher.publish] stockAdjustedEvent: $stockAdjustedEvent")
        val event = Event(
            UUID.randomUUID().toString(),
            EventType.PRODUCT_STOCK_ADJUSTED,
            stockAdjustedEvent,
        )
        kafkaTemplate.send(EventType.Topic.PRODUCT_STOCK_ADJUSTED, event.toJson())
    }
}
