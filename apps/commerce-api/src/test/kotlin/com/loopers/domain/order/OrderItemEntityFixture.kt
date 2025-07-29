package com.loopers.domain.order

import com.loopers.domain.order.vo.Quantity
import com.loopers.domain.vo.Price

class OrderItemEntityFixture {
    private var productId = 1L
    private var productName = "상품A"
    private var quantity = Quantity(1)
    private var price = Price(1000L)
    private var totalPrice = Price(1000L)

    companion object {
        fun anOrderItem(): OrderItemEntityFixture = OrderItemEntityFixture()
    }

    fun productId(productId: Long): OrderItemEntityFixture = apply { this.productId = productId }

    fun productName(productName: String): OrderItemEntityFixture = apply { this.productName = productName }

    fun quantity(quantity: Quantity): OrderItemEntityFixture = apply { this.quantity = quantity }

    fun price(price: Price): OrderItemEntityFixture = apply { this.price = price }

    fun totalPrice(totalPrice: Price): OrderItemEntityFixture = apply { this.totalPrice = totalPrice }

    fun build(): OrderItemEntity = OrderItemEntity(
        productId,
        productName,
        quantity,
        price,
        totalPrice,
    )
}
