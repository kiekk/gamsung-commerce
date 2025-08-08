package com.loopers.domain.coupon.policy

import com.loopers.domain.coupon.CouponEntity
import com.loopers.support.enums.coupon.CouponType
import org.springframework.stereotype.Component

@Component
class PercentRateCouponDiscountPolicy : CouponDiscountPolicy {

    override fun calculateDiscountAmount(coupon: CouponEntity, totalPrice: Long): Long {
        return ((totalPrice * coupon.discountRate.value) / 100).toLong()
    }

    override fun supports(type: CouponType): Boolean {
        return type == CouponType.PERCENTAGE
    }
}
