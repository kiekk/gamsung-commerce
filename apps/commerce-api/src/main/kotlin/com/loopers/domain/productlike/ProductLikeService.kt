package com.loopers.domain.productlike

import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
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
            productLikeCountRepository.findPessimisticLockedByProductId(created.productId)?.apply {
                increaseProductLikeCount()
            } ?: productLikeCountRepository.save(
                ProductLikeCountEntity(created.productId, 1),
            )
        }
    }

    @Transactional
    fun unlike(command: ProductLikeCommand.Unlike) {
        if (productLikeRepository.existsByUserIdAndProductId(command.userId, command.productId).not()) return

        val deleteCount =
            productLikeRepository.deleteByUserIdAndProductId(command.userId, command.productId)
        if (deleteCount == 0) return

        productLikeCountRepository.findPessimisticLockedByProductId(command.productId)?.decreaseProductLikeCount()
    }

    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(
            100,
            100,
            1000,
            1.5,
        ),
    )
    @Transactional
    fun likeOptimistic(command: ProductLikeCommand.Like) {
        if (productLikeRepository.existsByUserIdAndProductId(command.userId, command.productId)) return

        productLikeRepository.create(command.toEntity()).let { created ->
            productLikeCountRepository.findOptimisticLockedByProductId(created.productId)?.apply {
                increaseProductLikeCount()
            } ?: productLikeCountRepository.save(
                ProductLikeCountEntity(created.productId, 1),
            )
        }
    }

    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 3,
        backoff = Backoff(
            100,
            100,
            1000,
            1.5,
        ),
    )
    @Transactional
    fun unlikeOptimistic(command: ProductLikeCommand.Unlike) {
        if (productLikeRepository.existsByUserIdAndProductId(command.userId, command.productId).not()) return

        val deleteCount =
            productLikeRepository.deleteByUserIdAndProductId(command.userId, command.productId)
        if (deleteCount == 0) return

        productLikeCountRepository.findOptimisticLockedByProductId(command.productId)?.decreaseProductLikeCount()
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
