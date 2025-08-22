package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentJpaRepository : JpaRepository<PaymentEntity, Long> {
    fun findByOrderId(orderId: Long): PaymentEntity?

    fun findByTransactionKey(transactionKey: String): PaymentEntity?
}
