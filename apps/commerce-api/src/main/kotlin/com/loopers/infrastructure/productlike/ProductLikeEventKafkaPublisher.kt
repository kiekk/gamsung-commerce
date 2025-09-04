package com.loopers.infrastructure.productlike

import com.loopers.domain.productlike.ProductLikeEventPublisher
import com.loopers.event.Event
import com.loopers.event.EventType.PRODUCT_LIKED
import com.loopers.event.EventType.PRODUCT_UNLIKED
import com.loopers.event.EventType.Topic
import com.loopers.event.payload.productlike.ProductLikedEvent
import com.loopers.event.payload.productlike.ProductUnlikedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ProductLikeEventKafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<Any, Any>,
) : ProductLikeEventPublisher {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun publish(productLikedEvent: ProductLikedEvent) {
        log.info("[ProductLikeEventKafkaPublisher.publish] productLikeEvent: $productLikedEvent")
        val event = Event(
            UUID.randomUUID().toString(),
            PRODUCT_LIKED,
            ProductLikedEvent(productLikedEvent.productId),
        )
        kafkaTemplate.send(Topic.PRODUCT_V1_LIKE_CHANGED, productLikedEvent.productId.toString(), event.toJson())
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
                        Topic.PRODUCT_V1_LIKE_CHANGED_DLT,
                        productLikedEvent.productId.toString(),
                        event.toJson(),
                    )
                }
            }
    }

    override fun publish(productUnlikedEvent: ProductUnlikedEvent) {
        log.info("[ProductLikeEventKafkaPublisher.publish] productUnlikeEvent: $productUnlikedEvent")
        val event = Event(
            UUID.randomUUID().toString(),
            PRODUCT_UNLIKED,
            ProductUnlikedEvent(productUnlikedEvent.productId),
        )
        kafkaTemplate.send(Topic.PRODUCT_V1_LIKE_CHANGED, productUnlikedEvent.productId.toString(), event.toJson())
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
                        Topic.PRODUCT_V1_LIKE_CHANGED_DLT,
                        productUnlikedEvent.productId.toString(),
                        event.toJson(),
                    )
                }
            }
    }
}
