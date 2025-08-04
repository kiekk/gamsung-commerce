package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface ProductJpaRepository : JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    fun findByBrandIdAndName(brandId: Long, name: String): ProductEntity?
}
