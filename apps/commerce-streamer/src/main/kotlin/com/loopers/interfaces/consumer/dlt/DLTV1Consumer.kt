package com.loopers.interfaces.consumer.dlt

import com.loopers.event.EventType
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class DLTV1Consumer {

    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [
            EventType.Topic.PRODUCT_V1_CHANGED_DLT,
            EventType.Topic.PRODUCT_V1_STOCK_ADJUSTED_DLT,
            EventType.Topic.PRODUCT_V1_LIKE_CHANGED_DLT,
            EventType.Topic.PRODUCT_V1_VIEWED_DLT,
        ],
        groupId = EventType.Group.DLT_EVENTS,
    )
    fun listen(message: String, ack: Acknowledgment) {
        log.info("[DLTV1Consumer.listen] message: $message")
        ack.acknowledge()
    }
}
