package com.loopers.domain.product

interface ProductRepository {
    fun createProduct(product: ProductEntity): ProductEntity

    fun findByBrandIdAndName(brandId: Long, name: String): ProductEntity?

    fun findById(id: Long): ProductEntity?
}
