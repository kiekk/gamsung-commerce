package com.loopers.event.payload.payment

import com.loopers.event.payload.EventPayload

data class PaymentFailedEvent(
    val orderKey: String,
    val transactionKey: String,
) : EventPayload
