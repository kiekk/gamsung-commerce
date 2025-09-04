package com.loopers.domain.events

import com.loopers.event.EventType
import com.loopers.support.enums.EventHandleStatus

class EventHandledCommand {
    data class Succeed(
        val eventId: String,
        val eventType: EventType,
        val topic: String,
        val partitionNo: Int,
        val offsetNo: Long,
        val consumerGroup: String,
    ) {
        fun toEntity() = EventHandled(
            eventId,
            eventType,
            topic,
            partitionNo,
            offsetNo,
            consumerGroup,
            EventHandleStatus.SUCCEED,
        )
    }

    data class Failed(
        val eventId: String,
        val eventType: EventType,
        val topic: String,
        val partitionNo: Int,
        val offsetNo: Long,
        val consumerGroup: String,
    ) {
        fun toEntity() = EventHandled(
            eventId,
            eventType,
            topic,
            partitionNo,
            offsetNo,
            consumerGroup,
            EventHandleStatus.FAILED,
        )
    }
}
