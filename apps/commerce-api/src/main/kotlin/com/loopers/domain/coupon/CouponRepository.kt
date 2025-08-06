package com.loopers.domain.coupon

interface CouponRepository {
    fun createCoupon(coupon: CouponEntity): CouponEntity
}
