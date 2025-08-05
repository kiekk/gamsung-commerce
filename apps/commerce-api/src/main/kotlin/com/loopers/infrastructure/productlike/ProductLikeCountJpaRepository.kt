package com.loopers.infrastructure.productlike

import com.loopers.domain.productlike.ProductLikeCountEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock

interface ProductLikeCountJpaRepository : JpaRepository<ProductLikeCountEntity, Long> {
    fun findByProductId(productId: Long): ProductLikeCountEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findPessimisticLockedByProductId(productId: Long): ProductLikeCountEntity?

    @Lock(LockModeType.OPTIMISTIC)
    fun findOptimisticLockedByProductId(productId: Long): ProductLikeCountEntity?
}
