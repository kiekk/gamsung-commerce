package com.loopers.domain.coupon.policy.factory

import com.loopers.domain.coupon.CouponEntity
import com.loopers.domain.coupon.policy.CouponDiscountPolicy
import org.springframework.stereotype.Component

@Component
class CouponDiscountPolicyFactory(
    private val couponDiscountPolicies: List<CouponDiscountPolicy>,
) {

    fun calculateDiscountAmount(coupon: CouponEntity, totalPrice: Long): Long {
        return couponDiscountPolicies.find { it.supports(coupon.type) }?.calculateDiscountAmount(coupon, totalPrice)
            ?: throw IllegalArgumentException("지원하지 않는 쿠폰 타입입니다: ${coupon.type}")
    }
}
