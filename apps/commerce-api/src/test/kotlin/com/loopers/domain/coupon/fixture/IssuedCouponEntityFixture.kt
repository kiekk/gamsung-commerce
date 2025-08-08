package com.loopers.domain.coupon.fixture

import com.loopers.domain.coupon.IssuedCouponEntity

class IssuedCouponEntityFixture {
    var userId: Long = 1L
    var couponId: Long = 1L

    companion object {
        fun anIssuedCoupon(): IssuedCouponEntityFixture = IssuedCouponEntityFixture()
    }

    fun userId(userId: Long): IssuedCouponEntityFixture = apply { this.userId = userId }

    fun couponId(couponId: Long): IssuedCouponEntityFixture = apply { this.couponId = couponId }

    fun build(): IssuedCouponEntity = IssuedCouponEntity(
        userId,
        couponId,
    )
}
