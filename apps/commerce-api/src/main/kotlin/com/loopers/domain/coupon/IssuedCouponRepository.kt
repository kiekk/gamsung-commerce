package com.loopers.domain.coupon

interface IssuedCouponRepository {
    fun save(issuedCoupon: IssuedCouponEntity): IssuedCouponEntity

    fun findByIdWithPessimisticLock(id: Long): IssuedCouponEntity?

    fun getById(id: Long): IssuedCouponEntity
}
