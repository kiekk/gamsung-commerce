package com.loopers.event.payload.payment

import com.loopers.event.payload.EventPayload

class PaymentCompletedEvent(
    val orderKey: String,
    val transactionKey: String,
) : EventPayload
