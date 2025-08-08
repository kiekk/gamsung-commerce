package com.loopers.interfaces.api.coupon

import com.loopers.application.coupon.CouponFacade
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/coupons")
class CouponV1Controller(
    private val couponFacade: CouponFacade,
) : CouponV1ApiSpec {

    @PostMapping("")
    override fun createCoupon(
        @RequestBody request: CouponV1Dto.CreateRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<CouponV1Dto.CouponDetail> {
        val username = httpServletRequest.getHeader("X-USER-ID")
            ?: throw CoreException(ErrorType.BAD_REQUEST, "X-USER-ID가 존재하지 않습니다.")
        return couponFacade.createCoupon(request.toCommand(username))
            .let { CouponV1Dto.CouponDetail.from(it) }
            .let { ApiResponse.success(it) }
    }

    @PostMapping("/{couponId}/issue")
    override fun issueCoupon(
        @PathVariable("couponId") couponId: Long,
        @RequestBody request: CouponV1Dto.IssueRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<CouponV1Dto.IssuedCouponDetail> {
        val username = httpServletRequest.getHeader("X-USER-ID")
            ?: throw CoreException(ErrorType.BAD_REQUEST, "X-USER-ID가 존재하지 않습니다.")
        return couponFacade.issueCoupon(request.toCommand(username, couponId))
            .let { CouponV1Dto.IssuedCouponDetail.from(it) }
            .let { ApiResponse.success(it) }
    }
}
