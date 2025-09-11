package com.loopers.interfaces.consumer.productrank

import com.loopers.domain.productrank.ProductRankService
import com.loopers.event.Event
import com.loopers.event.EventType
import com.loopers.support.cache.productrank.ProductRankCacheKeyGenerator
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class ProductRankV1EventConsumer(
    private val productRankService: ProductRankService,
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(
        topics = [EventType.Topic.PRODUCT_V1_STOCK_ADJUSTED, EventType.Topic.PRODUCT_V1_LIKE_CHANGED, EventType.Topic.PRODUCT_V1_VIEWED],
        groupId = EventType.Group.PRODUCT_RANK_DAY_EVENTS,
    )
    @Transactional
    fun productRankDayEventListen(
        message: String,
        ack: Acknowledgment,
    ) {
        log.info("[ProductRankV1EventConsumer.productRankDayEventListen] message: $message")
        val event = Event.fromJson(message) ?: throw IllegalArgumentException("Invalid event message: $message")

        productRankService.handleEvent(
            ProductRankCacheKeyGenerator.generate(LocalDate.now()),
            event,
        )

        ack.acknowledge()
    }

    @KafkaListener(
        topics = [EventType.Topic.PRODUCT_V1_STOCK_ADJUSTED, EventType.Topic.PRODUCT_V1_LIKE_CHANGED, EventType.Topic.PRODUCT_V1_VIEWED],
        groupId = EventType.Group.PRODUCT_RANK_HOUR_EVENTS,
    )
    @Transactional
    fun productRankHourEventListen(
        message: String,
        ack: Acknowledgment,
    ) {
        log.info("[ProductRankV1EventConsumer.productRankHourEventListen] message: $message")
        val event = Event.fromJson(message) ?: throw IllegalArgumentException("Invalid event message: $message")

        productRankService.handleEvent(
            ProductRankCacheKeyGenerator.generate(LocalDateTime.now()),
            event,
        )

        ack.acknowledge()
    }
}
