package com.loopers.infrastructure.product

import com.loopers.domain.product.ProductLikeEntity
import com.loopers.domain.product.ProductLikeRepository
import org.springframework.stereotype.Repository

@Repository
class ProductLikeRepositoryImpl(
    private val productLikeJpaRepository: ProductLikeJpaRepository,
) : ProductLikeRepository {
    override fun create(productLike: ProductLikeEntity): ProductLikeEntity {
        return productLikeJpaRepository.save(productLike)
    }

    override fun deleteByUserIdAndProductId(userId: Long, productId: Long) {
        productLikeJpaRepository.deleteByUserIdAndProductId(userId, productId)
    }

    override fun existsByUserIdAndProductId(userId: Long, productId: Long): Boolean {
        return productLikeJpaRepository.existsByUserIdAndProductId(userId, productId)
    }

    override fun findByUserId(userId: Long): List<ProductLikeEntity> {
        return productLikeJpaRepository.findByUserId(userId)
    }

}
