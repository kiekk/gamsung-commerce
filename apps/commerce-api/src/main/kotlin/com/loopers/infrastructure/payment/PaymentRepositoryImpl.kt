package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentEntity
import com.loopers.domain.payment.PaymentRepository
import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryImpl(
    private val paymentJpaRepository: PaymentJpaRepository,
) : PaymentRepository {

    override fun save(payment: PaymentEntity): PaymentEntity {
        return paymentJpaRepository.save(payment)
    }

    override fun findWithItemsById(id: Long): PaymentEntity? {
        return paymentJpaRepository.findById(id).orElse(null)
    }

    override fun findWithItemsByTransactionKey(transactionKey: String): PaymentEntity? {
        return paymentJpaRepository.findByTransactionKey(transactionKey)
    }
}
