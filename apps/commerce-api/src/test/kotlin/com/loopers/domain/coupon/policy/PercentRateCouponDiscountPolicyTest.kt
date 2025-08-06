package com.loopers.domain.coupon.policy

import com.loopers.domain.coupon.CouponEntity
import com.loopers.domain.vo.PercentRate
import com.loopers.support.enums.coupon.CouponType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PercentRateCouponDiscountPolicyTest {

    private val percentRateCouponDiscountPolicy = PercentRateCouponDiscountPolicy()

    /*
     * 🧱 단위 테스트
     - [ ] 쿠폰 타입이 '비율'인 경우 총 금액에서 할인 금액을 차감한다.
     */
    @DisplayName("쿠폰 타입이 '비율'인 경우 총 금액에서 할인 금액을 차감한다.")
    @Test
    fun calculateDiscountAmount_shouldReturnCorrectDiscountAmountForPercentRateCouponType() {
        // arrange
        val coupon = CouponEntity(
            "비율 할인 쿠폰",
            CouponType.PERCENTAGE,
            discountRate = PercentRate(10.0),
        )
        val totalPrice = 5000L

        // act
        val discountAmount = percentRateCouponDiscountPolicy.calculateDiscountAmount(coupon, totalPrice)

        // assert
        assertThat(discountAmount).isEqualTo(((totalPrice * coupon.discountRate.value) / 100).toLong())
    }
}
