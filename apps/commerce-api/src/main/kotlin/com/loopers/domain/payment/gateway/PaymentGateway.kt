package com.loopers.domain.payment.gateway

interface PaymentGateway {
    fun requestPayment(userId: Long, command: PaymentGatewayCommand.Request): PaymentGatewayResult.Requested

    fun getPayment(userId: Long, transactionKey: String): PaymentGatewayResult.DetailResult
}
