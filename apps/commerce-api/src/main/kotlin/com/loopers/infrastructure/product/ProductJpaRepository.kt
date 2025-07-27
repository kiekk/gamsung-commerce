package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProductJpaRepository : JpaRepository<ProductEntity, Long> {
    fun findByBrandIdAndName(brandId: Long, name: String): ProductEntity?
}
