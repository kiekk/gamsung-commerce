package com.loopers.domain.productlike

import com.loopers.event.payload.productlike.ProductLikeEvent
import com.loopers.event.payload.productlike.ProductUnlikeEvent
import com.loopers.event.publisher.EventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductLikeService(
    private val productLikeRepository: ProductLikeRepository,
    private val productLikeCountRepository: ProductLikeCountRepository,
    private val eventPublisher: EventPublisher,
) {
    @Transactional
    fun like(command: ProductLikeCommand.Like) {
        if (productLikeRepository.existsByUserIdAndProductId(command.userId, command.productId)) return

        productLikeRepository.create(command.toEntity()).let { created ->
            productLikeCountRepository.findByProductIdWithPessimisticLock(created.productId)?.apply {
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

        productLikeCountRepository.findByProductIdWithPessimisticLock(command.productId)?.decreaseProductLikeCount()
    }

    @Transactional
    fun likeOptimistic(command: ProductLikeCommand.Like) {
        if (productLikeRepository.existsByUserIdAndProductId(command.userId, command.productId)) return

        productLikeRepository.create(command.toEntity()).let { created ->
            eventPublisher.publish(ProductLikeEvent(created.productId))
        }
    }

    @Transactional
    fun unlikeOptimistic(command: ProductLikeCommand.Unlike) {
        if (productLikeRepository.existsByUserIdAndProductId(command.userId, command.productId).not()) return

        val deleteCount =
            productLikeRepository.deleteByUserIdAndProductId(command.userId, command.productId)
        if (deleteCount == 0) return

        eventPublisher.publish(ProductUnlikeEvent(command.productId))
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
