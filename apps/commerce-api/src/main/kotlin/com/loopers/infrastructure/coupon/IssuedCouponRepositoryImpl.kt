package com.loopers.infrastructure.coupon

import com.loopers.domain.coupon.IssuedCouponEntity
import com.loopers.domain.coupon.IssuedCouponRepository
import org.springframework.stereotype.Repository

@Repository
class IssuedCouponRepositoryImpl(
    private val issuedCouponJpaRepository: IssuedCouponJpaRepository,
) : IssuedCouponRepository {
    override fun save(issuedCoupon: IssuedCouponEntity): IssuedCouponEntity {
        return issuedCouponJpaRepository.save(issuedCoupon)
    }

    override fun findById(id: Long): IssuedCouponEntity? {
        return issuedCouponJpaRepository.findById(id).orElse(null)
    }
}
