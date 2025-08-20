package com.loopers.infrastructure.payment.processor

import com.loopers.domain.order.OrderRepository
import com.loopers.domain.payment.PaymentCommand
import com.loopers.domain.payment.PaymentEntity
import com.loopers.domain.payment.PaymentRepository
import com.loopers.domain.payment.processor.PaymentProcessor
import com.loopers.domain.point.PointRepository
import com.loopers.domain.point.vo.Point
import com.loopers.support.enums.payment.PaymentMethodType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PointPaymentProcessor(
    private val paymentRepository: PaymentRepository,
    private val pointRepository: PointRepository,
    private val orderRepository: OrderRepository,
) : PaymentProcessor {

    @Transactional
    override fun pay(command: PaymentCommand.Pay): PaymentEntity {
        val point = pointRepository.findByUserIdWithLock(command.userId) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "사용자 포인트를 찾을 수 없습니다.",
        )

        val order = orderRepository.findWithItemsById(command.orderId) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "주문 정보를 찾을 수 없습니다.",
        )

        if (order.isNotEqualAmount(command.totalPrice)) {
            throw CoreException(
                ErrorType.BAD_REQUEST,
                "주문 총액과 결제 금액이 일치하지 않습니다. 주문 총액: ${order.amount.value}, 결제 금액: ${command.totalPrice.value}",
            )
        }

        if (point.cannotUsePoint(Point(command.totalPrice.value))) {
            throw CoreException(ErrorType.BAD_REQUEST, "포인트로 결제할 수 없습니다. 사용 가능한 포인트: ${point.point}")
        }

        point.usePoint(Point(command.totalPrice.value))
        return paymentRepository.save(command.toPaymentEntity().apply { complete() })
    }

    @Transactional
    override fun cancel(command: PaymentCommand.Cancel) {
        val point =
            pointRepository.findByUserIdWithLock(command.userId) ?: throw CoreException(
                ErrorType.NOT_FOUND,
                "사용자 포인트를 찾을 수 없습니다.",
            )
        val payment =
            paymentRepository.findWithItemsById(command.paymentId) ?: throw CoreException(
                ErrorType.NOT_FOUND,
                "결제 정보를 찾을 수 없습니다.",
            )

        point.refundPoint(Point(payment.totalPrice.value))
        payment.cancel()
    }

    override fun supports(method: PaymentMethodType): Boolean {
        return method == PaymentMethodType.POINT
    }
}
