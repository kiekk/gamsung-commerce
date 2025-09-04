package com.loopers.domain.events

interface EventHandledRepository {
    fun existsByEventIdAndConsumerGroup(eventId: String, consumerGroup: String): Boolean
    fun findByEventIdAndConsumerGroup(eventId: String, consumerGroup: String): EventHandled?
    fun save(eventHandled: EventHandled): EventHandled
}
