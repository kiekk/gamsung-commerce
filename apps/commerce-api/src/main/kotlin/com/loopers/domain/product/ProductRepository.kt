package com.loopers.domain.product

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification

interface ProductRepository {
    fun createProduct(product: ProductEntity): ProductEntity

    fun findByBrandIdAndName(brandId: Long, name: String): ProductEntity?

    fun findById(id: Long): ProductEntity?

    fun findAll(spec: Specification<ProductEntity>, pageRequest: PageRequest): Page<ProductEntity>
}
