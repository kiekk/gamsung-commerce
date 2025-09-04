package com.loopers.domain.events

import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class EventHandledService(
    private val eventHandledRepository: EventHandledRepository,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun isAlreadyHandled(eventId: String, consumerGroup: String): Boolean {
        log.info("[EventHandledService.isAlreadyHandled] eventId: $eventId, consumerGroup: $consumerGroup")
        return eventHandledRepository.existsByEventIdAndConsumerGroup(eventId, consumerGroup)
    }

    @Transactional
    fun markSuccess(command: EventHandledCommand.Succeed) {
        try {
            eventHandledRepository.save(command.toEntity())
        } catch (e: DataIntegrityViolationException) {
            log.debug("[EventHandledService.markSuccess] already exists command: {}", command)
        }
    }

    @Transactional
    fun markFail(command: EventHandledCommand.Failed) {
        try {
            eventHandledRepository.save(command.toEntity())
        } catch (e: DataIntegrityViolationException) {
            log.debug("[EventHandledService.markFail] already exists command: {}", command)
        }
    }
}
