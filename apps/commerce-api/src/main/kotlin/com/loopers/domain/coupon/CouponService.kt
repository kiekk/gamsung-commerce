package com.loopers.domain.coupon

import org.springframework.stereotype.Service

@Service
class CouponService(
    private val couponRepository: CouponRepository,
) {

    fun createCoupon(command: CouponCommand.Create): CouponEntity {
        return couponRepository.createCoupon(command.toEntity())
    }
}
