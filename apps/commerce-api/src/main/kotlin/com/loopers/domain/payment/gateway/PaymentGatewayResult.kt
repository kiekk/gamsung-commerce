package com.loopers.domain.payment.gateway

import com.loopers.support.enums.payment.PaymentStatusType
import com.loopers.support.enums.payment.TransactionStatus

class PaymentGatewayResult {
    data class Requested(
        val transactionKey: String? = null,
        val status: PaymentStatusType,
    )

    data class DetailResult(
        val transactionKey: String,
        val orderId: Long,
        val cardType: String,
        val cardNo: String,
        val amount: Long,
        val status: TransactionStatus,
        val reason: String,
    )

    data class ListResult(
        val orderId: Long,
        val transactions: List<TransactionResult>,
    ) {
        data class TransactionResult(
            val transactionKey: String,
            val status: TransactionStatus,
            val reason: String,
        )
    }
}
