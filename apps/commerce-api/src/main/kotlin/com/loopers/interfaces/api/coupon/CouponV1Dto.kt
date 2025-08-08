package com.loopers.interfaces.api.coupon

import com.loopers.application.coupon.CouponCriteria
import com.loopers.application.coupon.CouponInfo
import com.loopers.domain.vo.PercentRate
import com.loopers.domain.vo.Price
import com.loopers.support.enums.coupon.CouponStatusType
import com.loopers.support.enums.coupon.CouponType
import com.loopers.support.enums.coupon.IssuedCouponStatusType
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

class CouponV1Dto {
    data class CreateRequest(
        @field:NotBlank(message = "쿠폰명은 필수입니다.")
        val couponName: String,
        val couponType: CouponType,
        @field:Min(value = 0, message = "할인 금액은 0 이상이어야 합니다.")
        var discountAmount: Long = 0L,
        @field:Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
        var discountRate: Double = 0.0,
    ) {
        fun toCommand(username: String): CouponCriteria.Create {
            return CouponCriteria.Create(
                username,
                couponName,
                couponType,
                Price(discountAmount),
                PercentRate(discountRate),
            )
        }
    }

    data class IssueRequest(
        val userId: Long,
    ) {
        fun toCommand(username: String, couponId: Long): CouponCriteria.Issue {
            return CouponCriteria.Issue(
                username,
                userId,
                couponId,
            )
        }
    }

    data class CouponDetail(
        val id: Long,
        val name: String,
        val type: CouponType,
        val discountAmount: Long,
        val discountRate: Double,
        var status: CouponStatusType,
    ) {
        companion object {
            fun from(couponResult: CouponInfo.CouponResult): CouponDetail {
                return CouponDetail(
                    couponResult.id,
                    couponResult.name,
                    couponResult.type,
                    couponResult.discountAmount,
                    couponResult.discountRate,
                    couponResult.status,
                )
            }
        }
    }

    data class IssuedCouponDetail(
        val id: Long,
        val couponId: Long,
        val userId: Long,
        var status: IssuedCouponStatusType,
        var issuedAt: LocalDateTime?,
        var usedAt: LocalDateTime?,
    ) {
        companion object {
            fun from(issuedCouponResult: CouponInfo.IssuedCouponResult): IssuedCouponDetail {
                return IssuedCouponDetail(
                    issuedCouponResult.id,
                    issuedCouponResult.couponId,
                    issuedCouponResult.userId,
                    issuedCouponResult.status,
                    issuedCouponResult.issuedAt,
                    issuedCouponResult.usedAt,
                )
            }
        }
    }
}
