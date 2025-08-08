package com.loopers.domain.coupon.policy.factory

import com.loopers.domain.coupon.CouponEntity
import com.loopers.domain.vo.PercentRate
import com.loopers.domain.vo.Price
import com.loopers.support.enums.coupon.CouponType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CouponDiscountPolicyFactoryTest @Autowired constructor(
    private val couponDiscountPolicyFactory: CouponDiscountPolicyFactory,
) {

    /*
     **🔗 통합 테스트
    - [ ] 쿠폰 타입이 '정액'인 경우 총 금액에서 할인 금액을 차감한다.
    - [ ] 쿠폰 타입이 '비율'인 경우 총 금액에서 할인 금액을 차감한다.
     */
    @DisplayName("쿠폰 타입이 '정액'인 경우 총 금액에서 할인 금액을 차감한다.")
    @Test
    fun calculateDiscountAmount_shouldReturnCorrectDiscountAmountForFixedCouponType() {
        // arrange
        val coupon = CouponEntity("정액 할인 쿠폰", CouponType.FIXED, discountAmount = Price(1000L))
        val totalPrice = 5000L

        // act
        val discountAmount = couponDiscountPolicyFactory.calculateDiscountAmount(coupon, totalPrice)

        // assert
        assertThat(discountAmount).isEqualTo(totalPrice - coupon.discountAmount.value)
    }

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
        val discountAmount = couponDiscountPolicyFactory.calculateDiscountAmount(coupon, totalPrice)

        // assert
        assertThat(discountAmount).isEqualTo(((totalPrice * coupon.discountRate.value) / 100).toLong())
    }
}
