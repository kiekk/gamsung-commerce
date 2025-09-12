package com.loopers.interfaces.consumer.productrank

import com.loopers.config.kakfa.KafkaConfig
import com.loopers.domain.productrank.ProductRankService
import com.loopers.event.Event
import com.loopers.event.EventType
import com.loopers.support.cache.productrank.ProductRankCacheKeyGenerator
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class ProductRankV1EventConsumer(
    private val productRankService: ProductRankService,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [
            EventType.Topic.PRODUCT_V1_STOCK_ADJUSTED,
            EventType.Topic.PRODUCT_V1_LIKE_CHANGED,
            EventType.Topic.PRODUCT_V1_VIEWED,
        ],
        groupId = EventType.Group.PRODUCT_RANK_DAY_EVENTS,
        containerFactory = KafkaConfig.BATCH_LISTENER,
    )
    fun productRankDayEventListen(
        records: List<ConsumerRecord<String, String>>,
        ack: Acknowledgment,
    ) {
        log.info("[ProductRankV1EventConsumer.productRankDayEventListen] records: $records")

        val eventMap = records
            .map { Event.fromJson(it.value()) ?: throw IllegalArgumentException("Invalid event message: ${it.value()}") }
            .groupBy { it.eventType }

        eventMap.forEach { (eventType, events) ->
            productRankService.handleEvent(
                ProductRankCacheKeyGenerator.generate(LocalDate.now()),
                eventType,
                events.map { it.payload },
            )
        }

        ack.acknowledge()
    }

    @KafkaListener(
        topics = [
            EventType.Topic.PRODUCT_V1_STOCK_ADJUSTED,
            EventType.Topic.PRODUCT_V1_LIKE_CHANGED,
            EventType.Topic.PRODUCT_V1_VIEWED,
        ],
        groupId = EventType.Group.PRODUCT_RANK_HOUR_EVENTS,
        containerFactory = KafkaConfig.BATCH_LISTENER,
    )
    fun productRankHourEventListen(
        records: List<ConsumerRecord<String, String>>,
        ack: Acknowledgment,
    ) {
        log.info("[ProductRankV1EventConsumer.productRankHourEventListen] records: $records")

        val eventMap = records
            .map { Event.fromJson(it.value()) ?: throw IllegalArgumentException("Invalid event message: ${it.value()}") }
            .groupBy { it.eventType }

        eventMap.forEach { (eventType, events) ->
            productRankService.handleEvent(
                ProductRankCacheKeyGenerator.generate(LocalDateTime.now()),
                eventType,
                events.map { it.payload },
            )
        }

        ack.acknowledge()
    }
}
