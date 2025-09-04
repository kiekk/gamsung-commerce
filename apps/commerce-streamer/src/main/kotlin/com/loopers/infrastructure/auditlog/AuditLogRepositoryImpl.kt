package com.loopers.infrastructure.auditlog

import com.loopers.domain.auditlog.AuditLog
import com.loopers.domain.auditlog.AuditLogRepository
import org.springframework.stereotype.Repository

@Repository
class AuditLogRepositoryImpl(
    private val auditLogJpaRepository: AuditLogJpaRepository,
) : AuditLogRepository {
    override fun save(auditLog: AuditLog) {
        auditLogJpaRepository.save(auditLog)
    }
}
