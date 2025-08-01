package com.loopers.infrastructure.order

import com.loopers.domain.order.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OrderJpaRepository : JpaRepository<OrderEntity, Long> {
    @Query("""SELECT o FROM OrderEntity o LEFT JOIN FETCH o._orderItems WHERE o.id = :id""")
    fun findWithItemsById(id: Long): OrderEntity?
}
