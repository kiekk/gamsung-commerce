package com.loopers.application.coupon

import com.loopers.domain.coupon.CouponService
import com.loopers.domain.coupon.IssuedCouponService
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class CouponFacade(
    private val couponService: CouponService,
    private val issuedCouponService: IssuedCouponService,
    private val userService: UserService,
) {

    fun createCoupon(criteria: CouponCriteria.Create): CouponInfo.CouponDetail {
        userService.findUserBy(criteria.username)
            ?: throw CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다. username: ${criteria.username}")
        if (couponService.existsCouponByName(criteria.name)) {
            throw CoreException(ErrorType.CONFLICT, "이미 존재하는 쿠폰명입니다. 쿠폰명: ${criteria.name}")
        }
        return couponService.createCoupon(criteria.toCommand()).let {
            CouponInfo.CouponDetail.from(it)
        }
    }

    fun issueCoupon(criteria: CouponCriteria.Issue): CouponInfo.IssuedCouponDetail {
        userService.findUserBy(criteria.username)
            ?: throw CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다. username: ${criteria.username}")

        val coupon = couponService.findCouponById(criteria.couponId)
            ?: throw CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다. couponId: ${criteria.couponId}")

        if (coupon.isNotActive()) {
            throw CoreException(ErrorType.CONFLICT, "발급 가능한 쿠폰이 아닙니다. 쿠폰 상태: ${coupon.status}, couponId: ${criteria.couponId}")
        }

        userService.findUserBy(criteria.issuedUserId)
            ?: throw CoreException(ErrorType.NOT_FOUND, "쿠폰을 발급할 사용자를 찾을 수 없습니다. userId: ${criteria.issuedUserId}")

        return issuedCouponService.issueCoupon(criteria.toCommand()).let {
            CouponInfo.IssuedCouponDetail.from(it)
        }
    }
}
