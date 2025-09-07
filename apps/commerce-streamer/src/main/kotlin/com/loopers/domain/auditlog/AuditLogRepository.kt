package com.loopers.domain.auditlog

interface AuditLogRepository {
    fun save(auditLog: AuditLog)
}
