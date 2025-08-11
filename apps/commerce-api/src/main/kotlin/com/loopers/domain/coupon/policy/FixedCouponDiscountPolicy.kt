package com.loopers.domain.coupon.policy

import com.loopers.domain.coupon.CouponEntity
import com.loopers.support.enums.coupon.CouponType
import org.springframework.stereotype.Component

@Component
class FixedCouponDiscountPolicy : CouponDiscountPolicy {
    override fun calculateDiscountAmount(coupon: CouponEntity, totalPrice: Long): Long {
        return totalPrice - coupon.discountAmount.value
    }

    override fun supports(type: CouponType): Boolean {
        return type == CouponType.FIXED
    }
}
