package com.loopers.interfaces.catalog.consumer

import com.loopers.domain.catalog.CatalogService
import com.loopers.event.Event
import com.loopers.event.EventType.Group
import com.loopers.event.EventType.Topic
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class CatalogV1EventConsumer(
    private val catalogService: CatalogService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [Topic.PRODUCT_STOCK_ADJUSTED, Topic.PRODUCT_CHANGED, Topic.PRODUCT_LIKE_CHANGED],
        groupId = Group.CATALOG_EVENTS,
    )
    fun listen(message: String, ack: Acknowledgment) {
        log.info("[CatalogV1EventConsumer.listen] message: $message")
        val event = Event.fromJson(message)
        event?.let {
            catalogService.handleEvent(it)
        }
        ack.acknowledge()
    }
}
