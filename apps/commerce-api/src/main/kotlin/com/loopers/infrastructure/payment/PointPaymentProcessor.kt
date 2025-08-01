package com.loopers.infrastructure.payment

import com.loopers.domain.payment.PaymentProcessor
import com.loopers.domain.payment.PaymentProcessorCommand
import com.loopers.domain.payment.PaymentRepository
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
) : PaymentProcessor {

    @Transactional
    override fun process(command: PaymentProcessorCommand.Process) {
        val point =
            pointRepository.findByUserId(command.userId) ?: throw CoreException(ErrorType.NOT_FOUND, "사용자 포인트를 찾을 수 없습니다.")
        val payment =
            paymentRepository.findWithItemsByOrderId(command.paymentId) ?: throw CoreException(
                ErrorType.NOT_FOUND,
                "결제 정보를 찾을 수 없습니다.",
            )

        if (point.cannotUsePoint(Point(payment.totalAmount.value))) {
            throw CoreException(ErrorType.BAD_REQUEST, "포인트로 결제할 수 없습니다. 사용 가능한 포인트: ${point.point}")
        }

        point.usePoint(Point(payment.totalAmount.value))
        payment.complete()
    }

    @Transactional
    override fun cancel(command: PaymentProcessorCommand.Cancel) {
        val point =
            pointRepository.findByUserId(command.userId) ?: throw CoreException(ErrorType.NOT_FOUND, "사용자 포인트를 찾을 수 없습니다.")
        val payment =
            paymentRepository.findWithItemsByOrderId(command.paymentId) ?: throw CoreException(
                ErrorType.NOT_FOUND,
                "결제 정보를 찾을 수 없습니다.",
            )

        point.refundPoint(Point(payment.totalAmount.value))
        payment.cancel()
    }

    override fun supports(method: PaymentMethodType): Boolean {
        return method == PaymentMethodType.POINT
    }
}
