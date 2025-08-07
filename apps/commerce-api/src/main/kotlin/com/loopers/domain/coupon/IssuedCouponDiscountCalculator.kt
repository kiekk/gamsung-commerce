package com.loopers.domain.coupon

import com.loopers.domain.coupon.policy.factory.CouponDiscountPolicyFactory
import org.springframework.stereotype.Component

@Component
class IssuedCouponDiscountCalculator(
    private val couponRepository: CouponRepository,
    private val issuedCouponRepository: IssuedCouponRepository,
    private val couponDiscountPolicyFactory: CouponDiscountPolicyFactory,
) {
    fun calculate(issuedCouponId: Long?, totalPrice: Long): Long {
        if (issuedCouponId == null) {
            return 0L
        }

        val issuedCoupon = issuedCouponRepository.findByIdWithPessimisticLock(issuedCouponId)
            ?: throw IllegalArgumentException("발급된 쿠폰을 찾을 수 없습니다. issuedCouponId: $issuedCouponId")

        val coupon = couponRepository.findById(issuedCoupon.couponId)
            ?: throw IllegalArgumentException("쿠폰을 찾을 수 없습니다. couponId: ${issuedCoupon.couponId}")

        return couponDiscountPolicyFactory.calculateDiscountAmount(coupon, totalPrice)
    }
}
