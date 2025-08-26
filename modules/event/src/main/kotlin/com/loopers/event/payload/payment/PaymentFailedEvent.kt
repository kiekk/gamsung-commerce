package com.loopers.event.payload.payment

import com.loopers.event.payload.EventPayload

class PaymentFailedEvent(
    val orderKey: String,
    val transactionKey: String,
) : EventPayload
