package com.loopers.domain.coupon.fixture

import com.loopers.domain.coupon.CouponEntity
import com.loopers.domain.vo.PercentRate
import com.loopers.domain.vo.Price
import com.loopers.support.enums.coupon.CouponType

class CouponEntityFixture {
    private var name: String = "Test Coupon"
    private var type: CouponType = CouponType.FIXED
    private var discountAmount: Price = Price(1000L)
    private var discountRate: PercentRate = PercentRate(10.0)

    companion object {
        fun aCoupon(): CouponEntityFixture = CouponEntityFixture()
    }

    fun name(name: String): CouponEntityFixture = apply { this.name = name }

    fun type(type: CouponType): CouponEntityFixture = apply { this.type = type }

    fun discountAmount(discountAmount: Price): CouponEntityFixture = apply { this.discountAmount = discountAmount }

    fun discountRate(discountRate: PercentRate): CouponEntityFixture = apply { this.discountRate = discountRate }

    fun build(): CouponEntity {
        return CouponEntity(
            name,
            type,
            discountAmount,
            discountRate,
        )
    }
}
