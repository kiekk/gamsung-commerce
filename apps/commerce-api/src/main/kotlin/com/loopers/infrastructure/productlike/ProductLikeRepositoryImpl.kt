package com.loopers.infrastructure.productlike

import com.loopers.domain.productlike.ProductLikeEntity
import com.loopers.domain.productlike.ProductLikeRepository
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
