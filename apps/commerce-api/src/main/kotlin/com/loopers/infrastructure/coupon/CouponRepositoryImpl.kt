package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.CouponEntity
import com.loopers.domain.coupon.CouponRepository
import org.springframework.stereotype.Repository

@Repository
class CouponRepositoryImpl(
    private val couponJpaRepository: CouponJpaRepository,
) : CouponRepository {
    override fun createCoupon(coupon: CouponEntity): CouponEntity {
        return couponJpaRepository.save(coupon)
    }
}
