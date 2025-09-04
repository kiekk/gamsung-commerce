package com.loopers.domain.events

import com.loopers.domain.BaseEntity
import com.loopers.event.EventType
import com.loopers.support.enums.EventHandleStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "event_handled")
class EventHandled(
    val eventId: String,
    val eventType: EventType,
    val topic: String,
    val partitionNo: Int,
    val offsetNo: Long,
    val consumerGroup: String,
    @Enumerated(EnumType.STRING)
    var status: EventHandleStatus,
) : BaseEntity()
