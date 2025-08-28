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
        dataFlatformRepository.send(event)
    }

    @EventListener
    fun handle(event: PaymentCompletedEvent) {
        dataFlatformRepository.send(event)
    }

    @EventListener
    fun handle(event: PaymentFailedEvent) {
        dataFlatformRepository.send(event)
    }

    @EventListener
    fun handle(event: OrderCompletedEvent) {
        dataFlatformRepository.send(event)
    }
}
