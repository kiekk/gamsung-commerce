package com.loopers.domain.coupon

import com.loopers.domain.BaseEntity
import com.loopers.support.enums.coupon.IssuedCouponStatusType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "issued_coupon")
class IssuedCouponEntity(
    val couponId: Long,
    val userId: Long,
) : BaseEntity() {

    @Enumerated(EnumType.STRING)
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

    fun unUse() {
        status = IssuedCouponStatusType.ACTIVE
        usedAt = null
    }

    fun isUsed(): Boolean {
        return status == IssuedCouponStatusType.USED
    }
}
