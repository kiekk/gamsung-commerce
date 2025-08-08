package com.loopers.domain.coupon

interface CouponRepository {
    fun createCoupon(coupon: CouponEntity): CouponEntity

    fun findById(id: Long): CouponEntity?

    fun existsByName(name: String): Boolean
}
