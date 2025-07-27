package com.loopers.domain.stock

class StockEntityFixture(
    private var productId: Long = 1L,
    private var quantity: Int = 1,
) {

    companion object {
        fun aStock(): StockEntityFixture = StockEntityFixture()
    }

    fun productId(productId: Long): StockEntityFixture = apply { this.productId = productId }

    fun quantity(quantity: Int): StockEntityFixture = apply { this.quantity = quantity }

    fun build(): StockEntity = StockEntity(
        productId,
        quantity,
    )
}
