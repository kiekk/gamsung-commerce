package com.loopers.domain.coupon

import com.loopers.domain.coupon.fixture.CouponEntityFixture.Companion.aCoupon
import com.loopers.domain.vo.PercentRate
import com.loopers.domain.vo.Price
import com.loopers.support.enums.coupon.CouponStatusType
import com.loopers.support.enums.coupon.CouponType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CouponEntityTest {

    /*
     * 🧱 단위 테스트
    - [ ] 쿠폰명이 `20자 이내` 형식에 맞지 않으면, Coupon 객체 생성에 실패한다.
    - [ ] 쿠폰 정보가 유효하면 Coupon 객체 생성에 성공한다.
    - [ ] 쿠폰이 생성되면 쿠폰의 상태는 ACTIVE 여야 한다.
    - [ ] 쿠폰이 비활성화되면 쿠폰의 상태는 INACTIVE 여야 한다.
     */
    @DisplayName("Coupon 객체를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("쿠폰명이 `20자 이내` 형식에 맞지 않으면, Coupon 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(
            strings = [
                "abcdefghijabcdefghij1", // 21자
                "쿠폰이름이너무길어서실패합니다1234567890", // 21자
                " ", // 공백
                "", // 빈 문자열
            ],
        )
        fun failsToCreateCoupon_whenNameIsInvalid(invalidCouponName: String) {
            // arrange

            // act
            val exception = assertThrows<IllegalArgumentException> {
                aCoupon().name(invalidCouponName).build()
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
                { assertThat(exception.message).isEqualTo("쿠폰명은 1자 이상 20자 이하로 작성해야 합니다.") },
            )
        }

        @DisplayName("쿠폰 정보가 유효하면 Coupon 객체 생성에 성공한다.")
        @Test
        fun succeedsToCreateCoupon_whenInfoIsValid() {
            // arrange
            val validCouponName = "ValidCoupon123"

            // act
            val coupon = aCoupon()
                .name(validCouponName)
                .type(CouponType.FIXED)
                .discountAmount(Price(1000))
                .discountRate(PercentRate(10.0))
                .build()

            // assert
            assertThat(coupon.name).isEqualTo(validCouponName)
            assertThat(coupon.type).isEqualTo(CouponType.FIXED)
            assertThat(coupon.discountAmount.value).isEqualTo(1000)
            assertThat(coupon.discountRate.value).isEqualTo(10.0)
        }

        @DisplayName("쿠폰이 생성되면 쿠폰의 상태는 ACTIVE 여야 한다.")
        @Test
        fun couponShouldBeActiveWhenCreated() {
            // arrange
            val coupon = aCoupon()
                .name("ActiveCoupon")
                .type(CouponType.FIXED)
                .discountAmount(Price(1000))
                .discountRate(PercentRate(10.0))
                .build()

            // act & assert
            assertThat(coupon.status).isEqualTo(CouponStatusType.ACTIVE)
        }

        @DisplayName("쿠폰이 비활성화되면 쿠폰의 상태는 INACTIVE 여야 한다.")
        @Test
        fun couponShouldBeInactiveWhenDisabled() {
            // arrange
            val coupon = aCoupon()
                .name("InactiveCoupon")
                .type(CouponType.FIXED)
                .discountAmount(Price(1000))
                .discountRate(PercentRate(10.0))
                .build()
            coupon.inactive()

            // act & assert
            assertThat(coupon.status).isEqualTo(CouponStatusType.INACTIVE)
        }
    }

    /*
     * 🧱 단위 테스트
    - [ ] 쿠폰 타입이 '정액'인 경우 총 금액에서 할인 금액을 차감한다.
    - [ ] 쿠폰 타입이 '정률'인 경우 총 금액에서 할인 비율을 적용하여 할인 금액을 계산한다.
     */
    @DisplayName("쿠폰 할인 금액을 계산할 때, ")
    @Nested
    inner class CalculateDiscountAmount
}
