package com.loopers.domain.auditlog

import com.loopers.domain.BaseEntity
import com.loopers.event.EventType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "event_log")
class AuditLog(
    val eventId: String,
    @Enumerated(EnumType.STRING)
    val eventType: EventType,
    val topic: String,
    val partitionNo: Int,
    val offsetNo: Long,
    val payload: String,
) : BaseEntity()
