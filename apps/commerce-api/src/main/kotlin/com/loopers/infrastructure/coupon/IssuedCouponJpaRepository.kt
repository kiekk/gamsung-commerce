package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.IssuedCouponEntity
import org.springframework.data.jpa.repository.JpaRepository

interface IssuedCouponJpaRepository : JpaRepository<IssuedCouponEntity, Long> {
}
