package com.loopers.event.payload.order

import com.loopers.event.payload.EventPayload

data class OrderCompletedEvent(
    val orderKey: String,
    val transactionKey: String,
) : EventPayload
