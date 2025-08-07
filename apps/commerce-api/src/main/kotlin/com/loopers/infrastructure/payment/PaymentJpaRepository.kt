package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentJpaRepository : JpaRepository<PaymentEntity, Long> {
}
