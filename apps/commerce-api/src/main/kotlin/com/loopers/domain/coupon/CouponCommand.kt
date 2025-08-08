package com.loopers.domain.coupon

import com.loopers.domain.vo.PercentRate
import com.loopers.domain.vo.Price
import com.loopers.support.enums.coupon.CouponType

class CouponCommand {
    data class Create(
        val name: String,
        val couponType: CouponType,
        val discountAmount: Price = Price.ZERO,
        val discountRate: PercentRate = PercentRate.ZERO,
    ) {
        init {
            require(name.matches(NAME_PATTERN)) { "쿠폰명은 1자 이상 20자 이하로 작성해야 합니다." }
        }

        fun toEntity(): CouponEntity {
            return CouponEntity(
                name,
                couponType,
                discountAmount,
                discountRate,
            )
        }

        companion object {
            private val NAME_PATTERN = "^(?!\\s*$).{1,20}$".toRegex()
        }
    }
}
