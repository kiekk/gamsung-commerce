package com.loopers.interfaces.listener.dataflatform

import com.loopers.domain.dataflatform.DataFlatformRepository
import com.loopers.event.payload.order.OrderCompletedEvent
import com.loopers.event.payload.order.OrderCreatedEvent
import com.loopers.event.payload.payment.PaymentCompletedEvent
import com.loopers.event.payload.payment.PaymentFailedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DataFlatformEventListener(
    private val dataFlatformRepository: DataFlatformRepository,
) {
    @EventListener
    fun handle(event: OrderCreatedEvent) {
        try {
            dataFlatformRepository.send(event)
        } catch (e: Exception) {
            println("데이터플랫폼 전송 실패 event: $event, error: ${e.message}")
        }
    }

    @EventListener
    fun handle(event: PaymentCompletedEvent) {
        try {
            dataFlatformRepository.send(event)
        } catch (e: Exception) {
            println("데이터플랫폼 전송 실패 event: $event, error: ${e.message}")
        }
    }

    @EventListener
    fun handle(event: PaymentFailedEvent) {
        try {
            dataFlatformRepository.send(event)
        } catch (e: Exception) {
            println("데이터플랫폼 전송 실패 event: $event, error: ${e.message}")
        }
    }

    @EventListener
    fun handle(event: OrderCompletedEvent) {
        try {
            dataFlatformRepository.send(event)
        } catch (e: Exception) {
            println("데이터플랫폼 전송 실패 event: $event, error: ${e.message}")
        }
    }
}
