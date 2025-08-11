package com.loopers.domain.coupon

import com.loopers.domain.BaseEntity
import com.loopers.domain.vo.PercentRate
import com.loopers.domain.vo.Price
import com.loopers.support.enums.coupon.CouponStatusType
import com.loopers.support.enums.coupon.CouponType
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "coupon")
class CouponEntity(
    val name: String,
    @Enumerated(EnumType.STRING)
    val type: CouponType,
    @Embedded
    val discountAmount: Price = Price.ZERO,
    @Embedded
    val discountRate: PercentRate = PercentRate.ZERO,
) : BaseEntity() {

    @Enumerated(EnumType.STRING)
    var status: CouponStatusType

    init {
        require(name.matches(NAME_PATTERN)) { "쿠폰명은 1자 이상 20자 이하로 작성해야 합니다." }
        status = CouponStatusType.ACTIVE
    }

    fun inactive() {
        status = CouponStatusType.INACTIVE
    }

    fun isNotActive(): Boolean {
        return status != CouponStatusType.ACTIVE
    }

    companion object {
        private val NAME_PATTERN = "^(?!\\s*$).{1,20}$".toRegex()
    }
}
