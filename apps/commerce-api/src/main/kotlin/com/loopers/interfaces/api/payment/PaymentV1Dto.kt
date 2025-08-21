package com.loopers.interfaces.api.payment

import com.loopers.support.enums.payment.PaymentCardType
import com.loopers.support.enums.payment.TransactionStatus

class PaymentV1Dto {
    data class Callback(
        val transactionKey: String,
        val orderId: String,
        val cardType: PaymentCardType,
        val cardNo: String,
        val amount: Long,
        val status: TransactionStatus,
        val reason: String?,
    )
}
