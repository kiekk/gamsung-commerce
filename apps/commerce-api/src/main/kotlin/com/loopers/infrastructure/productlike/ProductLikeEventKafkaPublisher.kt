package com.loopers.infrastructure.productlike

import com.loopers.domain.productlike.ProductLikeEventPublisher
import com.loopers.event.Event
import com.loopers.event.EventType
import com.loopers.event.payload.productlike.ProductLikeChangedEvent
import com.loopers.event.payload.productlike.ProductLikeEvent
import com.loopers.event.payload.productlike.ProductUnlikeEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ProductLikeEventKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : ProductLikeEventPublisher {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun publish(productLikeEvent: ProductLikeEvent) {
        log.info("[ProductLikeEventKafkaPublisher.publish] productLikeEvent: $productLikeEvent")
        val event = Event(
            UUID.randomUUID().toString(),
            EventType.PRODUCT_LIKED,
            ProductLikeChangedEvent(productLikeEvent.productId),
        )
        kafkaTemplate.send(EventType.Topic.PRODUCT_LIKE_CHANGED, event.toJson())
    }

    override fun publish(productUnlikeEvent: ProductUnlikeEvent) {
        log.info("[ProductLikeEventKafkaPublisher.publish] productUnlikeEvent: $productUnlikeEvent")
        val event = Event(
            UUID.randomUUID().toString(),
            EventType.PRODUCT_UNLIKED,
            ProductLikeChangedEvent(productUnlikeEvent.productId),
        )
        kafkaTemplate.send(EventType.Topic.PRODUCT_LIKE_CHANGED, event.toJson())
    }
}
