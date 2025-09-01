package com.loopers.event.payload.payment

import com.loopers.event.payload.EventPayload

class PaymentFailedSuccessEvent(
    val orderKey: String,
    val transactionKey: String,
) : EventPayload
