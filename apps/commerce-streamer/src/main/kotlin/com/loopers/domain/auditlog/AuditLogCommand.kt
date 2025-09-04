package com.loopers.domain.auditlog

import com.loopers.event.EventType

class AuditLogCommand {
    data class Create(
        val eventId: String,
        val eventType: EventType,
        val topic: String,
        val partitionNo: Int,
        val offsetNo: Long,
        val payload: String,
    ) {
        fun toEntity(): AuditLog {
            return AuditLog(
                eventId,
                eventType,
                topic,
                partitionNo,
                offsetNo,
                payload,
            )
        }
    }
}
