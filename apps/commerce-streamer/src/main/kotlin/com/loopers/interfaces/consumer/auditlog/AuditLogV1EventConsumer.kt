package com.loopers.interfaces.consumer.auditlog

import com.loopers.domain.auditlog.AuditLogCommand
import com.loopers.domain.auditlog.AuditLogService
import com.loopers.event.Event
import com.loopers.event.EventType
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class AuditLogV1EventConsumer(
    private val auditLogService: AuditLogService,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [
            EventType.Topic.PRODUCT_V1_STOCK_ADJUSTED,
            EventType.Topic.PRODUCT_V1_CHANGED,
            EventType.Topic.PRODUCT_V1_LIKE_CHANGED,
            EventType.Topic.PRODUCT_V1_VIEWED,
        ],
        groupId = EventType.Group.AUDIT_LOG_EVENTS,
    )
    fun listen(
        message: String,
        ack: Acknowledgment,
        @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partition: Int,
        @Header(KafkaHeaders.OFFSET) offset: Long,
    ) {
        log.info("[AuditLogV1EventConsumer.listen] message: $message")
        log.info("Received message from topic: $topic, partition: $partition, offset: $offset")
        val event = Event.fromJson(message) ?: throw IllegalArgumentException("Invalid event message: $message")

        auditLogService.saveAuditLog(
            AuditLogCommand.Create(
                event.eventId,
                event.eventType,
                topic,
                partition,
                offset,
                message,
            ),
        )

        ack.acknowledge()
    }
}
