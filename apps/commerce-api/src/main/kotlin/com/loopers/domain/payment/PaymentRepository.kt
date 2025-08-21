package com.loopers.domain.payment

interface PaymentRepository {
    fun save(payment: PaymentEntity): PaymentEntity

    fun findWithItemsById(id: Long): PaymentEntity?

    fun findWithItemsByTransactionKey(transactionKey: String): PaymentEntity?
}
