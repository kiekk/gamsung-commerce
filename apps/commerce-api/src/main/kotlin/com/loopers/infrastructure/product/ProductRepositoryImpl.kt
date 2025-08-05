package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductEntity
import com.loopers.domain.product.ProductRepository
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val productJpaRepository: ProductJpaRepository,
) : ProductRepository {
    override fun save(product: ProductEntity): ProductEntity {
        return productJpaRepository.save(product)
    }

    override fun findByBrandIdAndName(brandId: Long, name: String): ProductEntity? {
        return productJpaRepository.findByBrandIdAndName(brandId, name)
    }

    override fun findById(id: Long): ProductEntity? {
        return productJpaRepository.findById(id).orElse(null)
    }

    override fun findByIds(productIds: List<Long>): List<ProductEntity> {
        return productJpaRepository.findAllById(productIds)
    }
}
