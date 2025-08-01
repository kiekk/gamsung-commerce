package com.loopers.domain.productlike

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductLikeService(
    private val productLikeRepository: ProductLikeRepository,
    private val productLikeCountRepository: ProductLikeCountRepository,
) {
    @Transactional
    fun like(command: ProductLikeCommand.Like) {
        if (productLikeRepository.existsByUserIdAndProductId(command.userId, command.productId)) return

        productLikeRepository.create(command.toEntity()).let { created ->
            productLikeCountRepository.findByProductId(created.productId)?.apply {
                increaseProductLikeCount()
            } ?: productLikeCountRepository.save(
                ProductLikeCountEntity(created.productId, 1),
            )
        }
    }

    @Transactional
    fun unlike(command: ProductLikeCommand.Unlike) {
        if (!productLikeRepository.existsByUserIdAndProductId(command.userId, command.productId)) return

        productLikeRepository.deleteByUserIdAndProductId(command.userId, command.productId).also {
            productLikeCountRepository.findByProductId(command.productId)?.decreaseProductLikeCount()
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
