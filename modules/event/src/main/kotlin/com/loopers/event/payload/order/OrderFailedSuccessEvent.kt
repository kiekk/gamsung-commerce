package com.loopers.event.payload.order

import com.loopers.event.payload.EventPayload

class OrderFailedSuccessEvent(
    val orderKey: String,
    val transactionKey: String,
) : EventPayload
