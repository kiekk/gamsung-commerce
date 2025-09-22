package com.loopers.event

import DataSerializer
import com.loopers.event.payload.EventPayload

class Event<T : EventPayload>(
    val eventId: String,
    val eventType: EventType,
    val payload: T,
) {

    fun toJson(): String {
        return DataSerializer.serialize(this)
    }

    companion object {
        fun of(eventId: String, eventType: EventType, payload: EventPayload): Event<EventPayload> {
            return Event(eventId, eventType, payload)
        }

        fun fromJson(json: String?): Event<EventPayload>? {
            val eventRaw = DataSerializer.deserialize(json, EventRaw::class.java) ?: return null
            val event: Event<EventPayload> = Event(
                eventRaw.eventId,
                EventType.valueOf(eventRaw.eventType),
                DataSerializer.deserialize(eventRaw.payload, EventType.valueOf(eventRaw.eventType).payloadClass),
            )
            return event
        }
    }

    data class EventRaw(
        val eventId: String,
        val eventType: String,
        val payload: Any,
    ) {
        fun toEvent(type: Class<EventPayload>): Event<EventPayload> {
            return of(
                eventId,
                EventType.valueOf(eventType),
                DataSerializer.deserialize(payload, type),
            )
        }
    }
}
