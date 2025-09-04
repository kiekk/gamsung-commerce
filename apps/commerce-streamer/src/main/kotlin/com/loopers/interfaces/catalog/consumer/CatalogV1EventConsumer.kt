package com.loopers.interfaces.catalog.consumer

import com.loopers.domain.catalog.CatalogService
import com.loopers.domain.events.EventHandledCommand
import com.loopers.domain.events.EventHandledService
import com.loopers.event.Event
import com.loopers.event.EventType.Group
import com.loopers.event.EventType.Topic
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CatalogV1EventConsumer(
    private val catalogService: CatalogService,
    private val eventHandledService: EventHandledService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [Topic.PRODUCT_V1_STOCK_SOLD_OUT, Topic.PRODUCT_V1_CHANGED, Topic.PRODUCT_V1_LIKE_CHANGED],
        groupId = Group.CATALOG_EVENTS,
    )
    @Transactional
    fun listen(
        message: String,
        ack: Acknowledgment,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
    ) {
        log.info("[CatalogV1EventConsumer.listen] message: $message")
        val event = Event.fromJson(message) ?: throw IllegalArgumentException("Invalid event message: $message")

        if (eventHandledService.isAlreadyHandled(event.eventId, Group.CATALOG_EVENTS)) {
            log.info("[CatalogV1EventConsumer.listen] already handled eventId: ${event.eventId}, group: ${Group.CATALOG_EVENTS}")
            ack.acknowledge()
            return
        }

        catalogService.handleEvent(event)
        eventHandledService.markSuccess(
            EventHandledCommand.Succeed(
                event.eventId,
                event.eventType,
                topic,
                partition,
                offset,
                Group.CATALOG_EVENTS,
            ),
        )
        ack.acknowledge()
    }
}
