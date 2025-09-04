package com.loopers.interfaces.dlt.consumer

import com.loopers.event.EventType.Group
import com.loopers.event.EventType.Topic
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class DLTV1Consumer {

    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [
            Topic.PRODUCT_V1_CHANGED_DLT,
            Topic.PRODUCT_V1_STOCK_ADJUSTED_DLT,
            Topic.PRODUCT_V1_LIKE_CHANGED_DLT,
            Topic.PRODUCT_V1_VIEWED_DLT,
        ],
        groupId = Group.DLT_EVENTS,
    )
    fun listen(message: String, ack: Acknowledgment) {
        log.info("[DLTV1Consumer.listen] message: $message")
        ack.acknowledge()
    }
}
