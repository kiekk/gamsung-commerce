package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductLikeCountEntity
import com.loopers.domain.product.ProductLikeCountRepository
import org.springframework.stereotype.Repository

@Repository
class ProductLikeCountRepositoryImpl(
    private val productLikeCountJpaRepository: ProductLikeCountJpaRepository,
) : ProductLikeCountRepository {
    override fun findByProductId(productId: Long): ProductLikeCountEntity? {
        return productLikeCountJpaRepository.findByProductId(productId)
    }

    override fun save(productLikeCountEntity: ProductLikeCountEntity): ProductLikeCountEntity {
        return productLikeCountJpaRepository.save(productLikeCountEntity)
    }

}
