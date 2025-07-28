package com.loopers.domain.product

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductLikeService(
    private val productLikeRepository: ProductLikeRepository,
    private val productLikeCountRepository: ProductLikeCountRepository,
) {
    @Transactional
    fun like(productLike: ProductLikeEntity) {
        if (productLikeRepository.existsByUserIdAndProductId(productLike.userId, productLike.productId)) return

        productLikeRepository.create(productLike).let { created ->
            productLikeCountRepository.findByProductId(created.productId)?.apply {
                increaseProductLikeCount()
            } ?: productLikeCountRepository.save(
                ProductLikeCountEntity(created.productId, 1),
            )
        }

    }

    @Transactional
    fun unlike(productLike: ProductLikeEntity) {
        if (!productLikeRepository.existsByUserIdAndProductId(productLike.userId, productLike.productId)) return

        productLikeRepository.deleteByUserIdAndProductId(productLike.userId, productLike.productId).also {
            productLikeCountRepository.findByProductId(productLike.productId)?.decreaseProductLikeCount()
        }
    }

    @Transactional(readOnly = true)
    fun getProductLikeCount(productId: Long): ProductLikeCountEntity? {
        return productLikeCountRepository.findByProductId(productId)
    }

    @Transactional(readOnly = true)
    fun getProductLikesByUserId(userId: Long): List<ProductLikeEntity> {
        return productLikeRepository.findByUserId(userId)
    }

}
