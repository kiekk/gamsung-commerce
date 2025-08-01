package com.loopers.domain.payment.fixture

import com.loopers.domain.payment.PaymentEntity
import com.loopers.domain.payment.PaymentEntity.PaymentMethodType

class PaymentEntityFixture {
    var orderId: Long = 1L
    var method: PaymentMethodType = PaymentMethodType.POINT

    companion object {
        fun aPayment(): PaymentEntityFixture = PaymentEntityFixture()
    }

    fun orderId(orderId: Long): PaymentEntityFixture = apply { this.orderId = orderId }

    fun method(method: PaymentMethodType): PaymentEntityFixture = apply { this.method = method }

    fun build(): PaymentEntity = PaymentEntity(
        orderId,
        method,
    )


}
