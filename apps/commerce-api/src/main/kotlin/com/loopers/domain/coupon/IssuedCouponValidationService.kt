package com.loopers.domain.coupon

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class IssuedCouponValidationService(
    private val issuedCouponRepository: IssuedCouponRepository,
) {

    fun validate(issuedCouponId: Long) {
        val issuedCoupon = (
            issuedCouponRepository.findByIdWithPessimisticLock(issuedCouponId)
            ?: throw CoreException(
                ErrorType.NOT_FOUND,
                "존재하지 않는 사용자 쿠폰입니다. issuedCouponId: $issuedCouponId",
            )
        )

        if (issuedCoupon.isUsed()) {
            throw CoreException(
                ErrorType.CONFLICT,
                "이미 사용한 사용자 쿠폰입니다. issuedCouponId: $issuedCouponId, 상태: ${issuedCoupon.status}",
            )
        }
    }
}
