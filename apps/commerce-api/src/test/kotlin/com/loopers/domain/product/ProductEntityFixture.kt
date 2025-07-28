package com.loopers.domain.product

import com.loopers.domain.vo.Price

class ProductEntityFixture {
    private var productId: Long = 1L
    private var brandId: Long = 1L
    private var name: String = "productName"
    private var description: String = "productDescription"
    private var price: Price = Price(1000L)
    private var status: ProductEntity.ProductStatusType = ProductEntity.ProductStatusType.ACTIVE

    companion object {
        fun aProduct(): ProductEntityFixture = ProductEntityFixture()
    }

    fun brandId(brandId: Long): ProductEntityFixture = apply { this.brandId = brandId }

    fun name(name: String): ProductEntityFixture = apply { this.name = name }

    fun description(description: String): ProductEntityFixture = apply { this.description = description }

    fun price(price: Price): ProductEntityFixture = apply { this.price = price }

    fun status(status: ProductEntity.ProductStatusType): ProductEntityFixture = apply { this.status = status }

    fun build(): ProductEntity = ProductEntity(
        brandId,
        name,
        description,
        price,
        status,
    )
}
