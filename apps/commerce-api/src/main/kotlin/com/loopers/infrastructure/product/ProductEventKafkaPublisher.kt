package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductEventPublisher
import com.loopers.event.Event
import com.loopers.event.EventType
import com.loopers.event.payload.product.ProductChangedEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ProductEventKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : ProductEventPublisher {
    override fun publish(productChangedEvent: ProductChangedEvent) {
        val event = Event(
            UUID.randomUUID().toString(),
            EventType.PRODUCT_CHANGED,
            productChangedEvent,
        )
        kafkaTemplate.send(EventType.Topic.PRODUCT_CHANGED, event.toJson())
    }
}
