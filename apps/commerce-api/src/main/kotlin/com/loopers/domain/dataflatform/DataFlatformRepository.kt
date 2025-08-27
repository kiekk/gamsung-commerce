package com.loopers.domain.dataflatform

import com.loopers.event.payload.EventPayload

interface DataFlatformRepository {
    fun send(event: EventPayload)
}
