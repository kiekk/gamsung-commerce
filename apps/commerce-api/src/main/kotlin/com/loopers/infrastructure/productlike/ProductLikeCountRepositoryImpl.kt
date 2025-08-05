package com.loopers.infrastructure.productlike

import com.loopers.domain.productlike.ProductLikeCountEntity
import com.loopers.domain.productlike.ProductLikeCountRepository
import org.springframework.stereotype.Repository

@Repository
class ProductLikeCountRepositoryImpl(
    private val productLikeCountJpaRepository: ProductLikeCountJpaRepository,
) : ProductLikeCountRepository {
    override fun findByProductId(productId: Long): ProductLikeCountEntity? {
        return productLikeCountJpaRepository.findByProductId(productId)
    }

    override fun findOptimisticLockedByProductId(productId: Long): ProductLikeCountEntity? {
        return productLikeCountJpaRepository.findOptimisticLockedByProductId(productId)
    }

    override fun findPessimisticLockedByProductId(productId: Long): ProductLikeCountEntity? {
        return productLikeCountJpaRepository.findPessimisticLockedByProductId(productId)
    }

    override fun save(productLikeCountEntity: ProductLikeCountEntity): ProductLikeCountEntity {
        return productLikeCountJpaRepository.save(productLikeCountEntity)
    }
}
