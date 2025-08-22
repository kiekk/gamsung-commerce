package com.loopers.interfaces.scheduler

import com.loopers.application.order.OrderFacade
import com.loopers.application.payment.PaymentCriteria
import com.loopers.application.payment.PaymentFacade
import com.loopers.support.enums.payment.PaymentStatusType
import com.loopers.support.enums.payment.TransactionStatus
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PaymentScheduler(
    private val orderFacade: OrderFacade,
    private val paymentFacade: PaymentFacade,
) {

    private val log = LoggerFactory.getLogger(PaymentScheduler::class.java)

    @Scheduled(cron = "0 */1 * * * ?")
    fun checkPaymentConsistency() {
        log.info("Starting payment consistency check...")
        val orders = orderFacade.findPendingOrders()

        orders.forEach { order ->
            val payment = paymentFacade.findPaymentByOrderId(order.orderId) ?: return@forEach

            if (payment.isCardType()) return@forEach

            order.orderKey?.let {
                val pgResult = paymentFacade.getPgResultByOrderId(order.userId, order.orderKey)
                if (pgResult.transactions.isEmpty()) return@forEach

                val pgResultTransaction = pgResult.transactions.first()

                paymentFacade.updatePayment(
                    PaymentCriteria.Update(
                        payment.id,
                        when (pgResultTransaction.status) {
                            TransactionStatus.SUCCESS -> PaymentStatusType.COMPLETED
                            TransactionStatus.FAILED -> PaymentStatusType.FAILED
                            else -> PaymentStatusType.FAILED
                        },
                        pgResultTransaction.transactionKey,
                    ),
                )

                when (pgResultTransaction.status) {
                    TransactionStatus.SUCCESS -> orderFacade.handlePaymentCompleted(order.orderKey, payment.transactionKey)
                    TransactionStatus.FAILED -> orderFacade.handlePaymentFailed(order.orderKey, payment.transactionKey)
                    else -> {}
                }
            }
        }
        log.info("Payment consistency check completed.")
    }
}
