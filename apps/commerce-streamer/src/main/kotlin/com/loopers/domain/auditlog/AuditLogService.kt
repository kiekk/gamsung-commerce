package com.loopers.domain.auditlog

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AuditLogService(
    private val auditLogRepository: AuditLogRepository,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun saveAuditLog(command: AuditLogCommand.Create) {
        log.info("[AuditLogService.saveAuditLog] command: $command")
        auditLogRepository.save(command.toEntity())
    }
}
