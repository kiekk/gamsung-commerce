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

    fun useIssuedCoupon(id: Long?) {
        id?.let {
            issuedCouponRepository.findByIdWithPessimisticLock(it)?.use()
        }
    }

    fun unUseIssuedCoupon(id: Long?) {
        id?.let {
            issuedCouponRepository.findByIdWithPessimisticLock(it)?.unUse()
        }
    }
}
