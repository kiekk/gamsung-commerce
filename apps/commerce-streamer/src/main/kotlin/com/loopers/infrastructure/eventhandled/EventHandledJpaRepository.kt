package com.loopers.infrastructure.eventhandled

import com.loopers.domain.events.EventHandled
import org.springframework.data.jpa.repository.JpaRepository

interface EventHandledJpaRepository : JpaRepository<EventHandled, Long> {
    fun existsByEventIdAndConsumerGroup(eventId: String, consumerGroup: String): Boolean
    fun findByEventIdAndConsumerGroup(eventId: String, consumerGroup: String): EventHandled?
}
