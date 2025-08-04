package com.loopers.support.error.payment

import com.loopers.support.error.ErrorType

class PaymentException(
    val errorType: ErrorType,
    val customMessage: String? = null,
) : RuntimeException(customMessage ?: errorType.message)
