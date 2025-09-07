package com.loopers.interfaces.listener.dataflatform

import com.loopers.domain.dataflatform.DataFlatformRepository
import com.loopers.event.payload.EventPayload
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DataFlatformEventListener(
    private val dataFlatformRepository: DataFlatformRepository,
) {

    @EventListener
    fun handle(event: EventPayload) {
        try {
            dataFlatformRepository.send(event)
        } catch (e: Exception) {
            println("데이터플랫폼 전송 실패 event: $event, error: ${e.message}")
        }
    }
}
