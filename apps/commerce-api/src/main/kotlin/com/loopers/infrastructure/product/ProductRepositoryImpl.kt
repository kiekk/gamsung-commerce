package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductEntity
import com.loopers.domain.product.ProductRepository
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val productJpaRepository: ProductJpaRepository,
) : ProductRepository {
    override fun createProduct(product: ProductEntity): ProductEntity {
        return productJpaRepository.save(product)
    }

    override fun findByBrandIdAndName(brandId: Long, name: String): ProductEntity? {
        return productJpaRepository.findByBrandIdAndName(brandId, name)
    }
}
