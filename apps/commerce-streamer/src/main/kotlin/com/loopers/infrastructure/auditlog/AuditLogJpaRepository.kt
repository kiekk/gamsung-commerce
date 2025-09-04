package com.loopers.infrastructure.auditlog

import com.loopers.domain.auditlog.AuditLog
import org.springframework.data.jpa.repository.JpaRepository

interface AuditLogJpaRepository : JpaRepository<AuditLog, Long>
