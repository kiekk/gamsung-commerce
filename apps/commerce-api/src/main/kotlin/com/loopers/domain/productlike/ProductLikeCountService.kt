package com.loopers.domain.productlike

import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductLikeCountService(
    private val productLikeCountRepository: ProductLikeCountRepository,
) {

    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 1,
        backoff = Backoff(delay = 10, multiplier = 1.0),
    )
    @Transactional
    fun increase(productId: Long) {
        productLikeCountRepository.findByProductIdWithOptimisticLock(productId)?.apply {
            increaseProductLikeCount()
        } ?: productLikeCountRepository.save(
            ProductLikeCountEntity(productId, 1),
        )
    }

    @Retryable(
        value = [OptimisticLockingFailureException::class],
        maxAttempts = 1,
        backoff = Backoff(delay = 10, multiplier = 1.0),
    )
    @Transactional
    fun decrease(productId: Long) {
        productLikeCountRepository.findByProductIdWithOptimisticLock(productId)?.decreaseProductLikeCount()
    }
}
