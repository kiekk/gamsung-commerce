package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.CouponEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CouponJpaRepository : JpaRepository<CouponEntity, Long> {
    fun existsByName(name: String): Boolean
}
