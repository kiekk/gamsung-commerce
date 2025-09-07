package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductEventPublisher
import com.loopers.event.Event
import com.loopers.event.EventType
import com.loopers.event.EventType.Topic
import com.loopers.event.payload.product.ProductChangedEvent
import com.loopers.event.payload.product.ProductViewedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ProductEventKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<Any, Any>,
) : ProductEventPublisher {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun publish(productViewedEvent: ProductViewedEvent) {
        log.info("[ProductEventKafkaPublisher.publish] productViewedEvent: $productViewedEvent")
        val event = Event(
            UUID.randomUUID().toString(),
            EventType.PRODUCT_VIEWED,
            productViewedEvent,
        )
        kafkaTemplate.send(Topic.PRODUCT_V1_VIEWED, productViewedEvent.productId.toString(), event.toJson())
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
                        Topic.PRODUCT_V1_VIEWED_DLT,
                        productViewedEvent.productId.toString(),
                        event.toJson(),
                    )
                }
            }
    }

    override fun publish(productChangedEvent: ProductChangedEvent) {
        log.info("[ProductEventKafkaPublisher.publish] productChangedEvent: $productChangedEvent")
        val event = Event(
            UUID.randomUUID().toString(),
            EventType.PRODUCT_CHANGED,
            productChangedEvent,
        )
        kafkaTemplate.send(Topic.PRODUCT_V1_CHANGED, productChangedEvent.productId.toString(), event.toJson())
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
                        Topic.PRODUCT_V1_CHANGED_DLT,
                        productChangedEvent.productId.toString(),
                        event.toJson(),
                    )
                }
            }
    }
}
