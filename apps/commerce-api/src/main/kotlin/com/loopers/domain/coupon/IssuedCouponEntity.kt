package com.loopers.domain.coupon

import com.loopers.domain.BaseEntity
import com.loopers.support.enums.coupon.IssuedCouponStatusType
import java.time.LocalDateTime

class IssuedCouponEntity(
    val couponId: Long,
    val userId: Long,
) : BaseEntity() {

    var status: IssuedCouponStatusType
    var issuedAt: LocalDateTime? = null
    var usedAt: LocalDateTime? = null

    init {
        require(couponId > 0) { "쿠폰 ID는 0보다 커야 합니다." }
        require(userId > 0) { "사용자 ID는 0보다 커야 합니다." }
        status = IssuedCouponStatusType.ACTIVE
        issuedAt = LocalDateTime.now()
    }

    fun use() {
        status = IssuedCouponStatusType.USED
        usedAt = LocalDateTime.now()
    }
}
