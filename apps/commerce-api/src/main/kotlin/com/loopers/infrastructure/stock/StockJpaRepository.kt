package com.loopers.infrastructure.stock

import com.loopers.domain.stock.StockEntity
import org.springframework.data.jpa.repository.JpaRepository

interface StockJpaRepository : JpaRepository<StockEntity, Long> {
    fun findByProductId(productId: Long): StockEntity?
}
