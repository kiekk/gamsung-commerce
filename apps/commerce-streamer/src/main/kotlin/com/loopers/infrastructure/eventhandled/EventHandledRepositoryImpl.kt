package com.loopers.infrastructure.eventhandled

import com.loopers.domain.events.EventHandled
import com.loopers.domain.events.EventHandledRepository
import org.springframework.stereotype.Repository

@Repository
class EventHandledRepositoryImpl(
    private val eventHandledJpaRepository: EventHandledJpaRepository,
) : EventHandledRepository {
    override fun existsByEventIdAndConsumerGroup(eventId: String, consumerGroup: String): Boolean {
        return eventHandledJpaRepository.existsByEventIdAndConsumerGroup(eventId, consumerGroup)
    }

    override fun findByEventIdAndConsumerGroup(eventId: String, consumerGroup: String): EventHandled? {
        return eventHandledJpaRepository.findByEventIdAndConsumerGroup(eventId, consumerGroup)
    }

    override fun save(eventHandled: EventHandled): EventHandled {
        return eventHandledJpaRepository.save(eventHandled)
    }
}
