package com.loopers.infrastructure.productlike

import com.loopers.domain.productlike.ProductLikeCountEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface ProductLikeCountJpaRepository : JpaRepository<ProductLikeCountEntity, Long> {
    fun findByProductId(productId: Long): ProductLikeCountEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT plc FROM ProductLikeCountEntity plc WHERE plc.productId = :productId")
    fun findByProductIdWithPessimisticLock(productId: Long): ProductLikeCountEntity?

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT plc FROM ProductLikeCountEntity plc WHERE plc.productId = :productId")
    fun findByProductIdWithOptimisticLock(productId: Long): ProductLikeCountEntity?
}
