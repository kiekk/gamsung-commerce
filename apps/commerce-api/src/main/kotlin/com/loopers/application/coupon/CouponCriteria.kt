package com.loopers.application.coupon

import com.loopers.domain.coupon.CouponCommand
import com.loopers.domain.coupon.IssuedCouponCommand
import com.loopers.domain.vo.PercentRate
import com.loopers.domain.vo.Price
import com.loopers.support.enums.coupon.CouponType

class CouponCriteria {
    data class Create(
        val username: String,
        val name: String,
        val type: CouponType,
        val discountAmount: Price,
        val discountRate: PercentRate,
    ) {
        fun toCommand(): CouponCommand.Create {
            return CouponCommand.Create(
                name,
                type,
                discountAmount,
                discountRate,
            )
        }
    }

    data class Issue(
        val username: String,
        val issuedUserId: Long,
        val couponId: Long,
    ) {
        fun toCommand(): IssuedCouponCommand.Issue {
            return IssuedCouponCommand.Issue(
                issuedUserId,
                couponId,
            )
        }
    }
}
