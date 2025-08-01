package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface PaymentJpaRepository : JpaRepository<PaymentEntity, Long> {
    @Query("""SELECT p FROM PaymentEntity p LEFT JOIN FETCH p._paymentItems WHERE p.orderId = :orderId""")
    fun findWithItemsByOrderId(orderId: Long) : PaymentEntity?
}
