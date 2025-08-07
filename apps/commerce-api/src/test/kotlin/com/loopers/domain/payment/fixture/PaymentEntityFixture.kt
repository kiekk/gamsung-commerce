package com.loopers.domain.payment.fixture

import com.loopers.domain.payment.PaymentEntity
import com.loopers.domain.vo.Price
import com.loopers.support.enums.payment.PaymentMethodType

class PaymentEntityFixture {
    var orderId: Long = 1L
    var method: PaymentMethodType = PaymentMethodType.POINT
    var totalPrice: Price = Price(3000L)

    companion object {
        fun aPayment(): PaymentEntityFixture = PaymentEntityFixture()
    }

    fun orderId(orderId: Long): PaymentEntityFixture = apply { this.orderId = orderId }

    fun method(method: PaymentMethodType): PaymentEntityFixture = apply { this.method = method }

    fun totalPrice(totalPrice: Price): PaymentEntityFixture = apply { this.totalPrice = totalPrice }

    fun build(): PaymentEntity = PaymentEntity(
        orderId,
        method,
        totalPrice
    )
}
