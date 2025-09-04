package com.loopers.infrastructure.stock

import com.loopers.domain.stock.StockEventPublisher
import com.loopers.event.Event
import com.loopers.event.EventType
import com.loopers.event.EventType.Topic
import com.loopers.event.payload.stock.StockAdjustedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class StockAdjustedEventKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<Any, Any>,
) : StockEventPublisher {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun publish(stockAdjustedEvent: StockAdjustedEvent) {
        log.info("[StockAdjustedEventKafkaPublisher.publish] stockAdjustedEvent: $stockAdjustedEvent")
        val event = Event(
            UUID.randomUUID().toString(),
            EventType.PRODUCT_STOCK_ADJUSTED,
            stockAdjustedEvent,
        )
        kafkaTemplate.send(Topic.PRODUCT_V1_STOCK_ADJUSTED, stockAdjustedEvent.productId.toString(), event.toJson())
            .whenComplete { result, ex ->
                if (ex == null) {
                    val meta = result.recordMetadata
                    log.info(
                        "success to send message | topic={}, partition={}, offset={}",
                        meta.topic(),
                        meta.partition(),
                        meta.offset(),
                    )
                } else {
                    log.error(
                        "fail to send message | topic= {}, partition= {}, offset= {}, ex= {}",
                        result?.recordMetadata?.topic(),
                        result?.recordMetadata?.partition(),
                        result?.recordMetadata?.offset(),
                        ex.message,
                        ex,
                    )

                    // DLT 토픽 발행
                    kafkaTemplate.send(
                        Topic.PRODUCT_V1_STOCK_ADJUSTED_DLT,
                        stockAdjustedEvent.productId.toString(),
                        event.toJson(),
                    )
                }
            }
    }
}
