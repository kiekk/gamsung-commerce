package com.loopers.event.payload.order

import com.loopers.event.payload.EventPayload

class OrderCreatedEvent(
    val orderId: Long,
    val userId: Long,
    val totalPrice: Long,
    val paymentMethod: String,
    val cardType: String? = null,
    val cardNo: String? = null,
    val orderKey: String? = null,
) : EventPayload
