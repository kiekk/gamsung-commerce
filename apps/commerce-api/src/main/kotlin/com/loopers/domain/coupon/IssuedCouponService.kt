package com.loopers.domain.coupon

import org.springframework.stereotype.Service

@Service
class IssuedCouponService(
    private val issuedCouponRepository: IssuedCouponRepository,
) {

    fun issueCoupon(command: IssuedCouponCommand.Issue): IssuedCouponEntity {
        return issuedCouponRepository.save(command.toEntity())
    }

    fun findIssuedCouponById(id: Long): IssuedCouponEntity? {
        return issuedCouponRepository.findById(id)
    }
}
