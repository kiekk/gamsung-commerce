package com.loopers.domain.coupon.policy

import com.loopers.domain.coupon.CouponEntity
import com.loopers.support.enums.coupon.CouponType

interface CouponDiscountPolicy {
    fun calculateDiscountAmount(coupon: CouponEntity, totalPrice: Long): Long

    fun supports(type: CouponType): Boolean
}
