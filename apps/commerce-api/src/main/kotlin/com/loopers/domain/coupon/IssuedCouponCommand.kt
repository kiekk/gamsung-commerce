package com.loopers.domain.coupon

class IssuedCouponCommand {
    data class Issue(
        val userId: Long,
        val couponId: Long,
    ) {
        fun toEntity(): IssuedCouponEntity {
            return IssuedCouponEntity(
                userId,
                couponId,
            )
        }
    }
}
