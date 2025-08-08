package com.loopers.application.coupon

import com.loopers.domain.coupon.CouponEntity
import com.loopers.domain.coupon.IssuedCouponEntity
import com.loopers.support.enums.coupon.CouponStatusType
import com.loopers.support.enums.coupon.CouponType
import com.loopers.support.enums.coupon.IssuedCouponStatusType
import java.time.LocalDateTime

class CouponInfo {
    data class CouponResult(
        val id: Long,
        val name: String,
        val type: CouponType,
        val discountAmount: Long,
        val discountRate: Double,
        var status: CouponStatusType,
    ) {
        companion object {
            fun from(couponEntity: CouponEntity): CouponResult {
                return CouponResult(
                    couponEntity.id,
                    couponEntity.name,
                    couponEntity.type,
                    couponEntity.discountAmount.value,
                    couponEntity.discountRate.value,
                    couponEntity.status,
                )
            }
        }
    }

    data class IssuedCouponResult(
        val id: Long,
        val couponId: Long,
        val userId: Long,
        var status: IssuedCouponStatusType,
        var issuedAt: LocalDateTime?,
        var usedAt: LocalDateTime?,
    ) {
        companion object {
            fun from(issuedCouponEntity: IssuedCouponEntity): IssuedCouponResult {
                return IssuedCouponResult(
                    issuedCouponEntity.id,
                    issuedCouponEntity.couponId,
                    issuedCouponEntity.userId,
                    issuedCouponEntity.status,
                    issuedCouponEntity.issuedAt,
                    issuedCouponEntity.usedAt,
                )
            }
        }
    }
}
