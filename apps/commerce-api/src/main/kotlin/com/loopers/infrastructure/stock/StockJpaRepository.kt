package com.loopers.infrastructure.stock

import com.loopers.domain.stock.StockEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface StockJpaRepository : JpaRepository<StockEntity, Long> {
    fun findByProductId(productId: Long): StockEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM StockEntity s WHERE s.productId IN :productIds")
    fun findAllProductIdsWithLock(productIds: List<Long>): List<StockEntity>
}
