package com.loopers.domain.coupon

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IssuedCouponService(
    private val issuedCouponRepository: IssuedCouponRepository,
) {

    fun issueCoupon(command: IssuedCouponCommand.Issue): IssuedCouponEntity {
        return issuedCouponRepository.save(command.toEntity())
    }

    fun findIssuedCouponById(id: Long): IssuedCouponEntity? {
        return issuedCouponRepository.findByIdWithPessimisticLock(id)
    }

    fun useIssuedCoupon(id: Long) {
        issuedCouponRepository.getById(id).use()
    }
}
