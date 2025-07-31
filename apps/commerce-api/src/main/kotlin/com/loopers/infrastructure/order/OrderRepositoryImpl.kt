package com.loopers.infrastructure.order

import com.loopers.domain.order.OrderEntity
import com.loopers.domain.order.OrderRepository
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(
    private val orderJpaRepository: OrderJpaRepository,
) : OrderRepository {

    override fun save(order: OrderEntity): OrderEntity {
        return orderJpaRepository.save(order)
    }

    override fun findById(id: Long): OrderEntity? {
        return orderJpaRepository.findById(id).orElse(null)
    }
}
