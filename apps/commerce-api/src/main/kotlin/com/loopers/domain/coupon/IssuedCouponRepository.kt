package com.loopers.domain.coupon

interface IssuedCouponRepository {
    fun save(issuedCoupon: IssuedCouponEntity): IssuedCouponEntity

    fun findById(id: Long): IssuedCouponEntity?
}
