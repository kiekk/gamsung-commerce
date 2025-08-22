package com.loopers.application.payment

import com.loopers.domain.payment.PaymentCommand
import com.loopers.support.enums.payment.PaymentStatusType

class PaymentCriteria {
    data class Update(
        val id: Long,
        val status: PaymentStatusType,
        val transactionKey: String,
    ) {

        fun toCommand(): PaymentCommand.Update {
            return PaymentCommand.Update(
                id,
                status,
                transactionKey,
            )
        }
    }
}
