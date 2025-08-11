package com.loopers.domain.product

interface ProductRepository {
    fun save(product: ProductEntity): ProductEntity

    fun findByBrandIdAndName(brandId: Long, name: String): ProductEntity?

    fun findById(id: Long): ProductEntity?

    fun findByIds(productIds: List<Long>): List<ProductEntity>
}
