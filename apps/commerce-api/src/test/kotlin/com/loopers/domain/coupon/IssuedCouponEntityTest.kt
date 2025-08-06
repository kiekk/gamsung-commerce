package com.loopers.domain.coupon

import com.loopers.support.enums.coupon.IssuedCouponStatusType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IssuedCouponEntityTest {

    /*
    * 🧱 단위 테스트
    - [ ] 사용자 쿠폰이 생성되면 쿠폰의 상태는 ACTIVE, 발급일도 설정 되어야 한다.
    - [ ] 사용자 쿠폰이 사용되면 쿠폰의 상태는 USED, 사용일도 설정 되어야 한다.
     */
    @DisplayName("사용자 쿠폰을 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("사용자 쿠폰이 생성되면 쿠폰의 상태는 ACTIVE, 발급일도 설정 되어야 한다.")
        @Test
        fun succeedsToCreateIssuedCoupon_whenInfoIsValid() {
            // arrange
            val couponId = 1L
            val userId = 1L

            // act
            val issuedCoupon = IssuedCouponEntity(couponId, userId)

            // assert
            assertAll(
                { assertThat(issuedCoupon.issuedAt).isNotNull() },
                { assertThat(issuedCoupon.status).isEqualTo(IssuedCouponStatusType.ACTIVE) },
                { assertThat(issuedCoupon.couponId).isEqualTo(couponId) },
                { assertThat(issuedCoupon.userId).isEqualTo(userId) },
            )
        }

        @DisplayName("사용자 쿠폰이 사용되면 쿠폰의 상태는 USED, 사용일도 설정 되어야 한다.")
        @Test
        fun succeedsToUseIssuedCoupon() {
            // arrange
            val couponId = 1L
            val userId = 1L
            val issuedCoupon = IssuedCouponEntity(couponId, userId)

            // act
            issuedCoupon.use()

            // assert
            assertAll(
                { assertThat(issuedCoupon.usedAt).isNotNull() },
                { assertThat(issuedCoupon.status).isEqualTo(IssuedCouponStatusType.USED) },
                { assertThat(issuedCoupon.couponId).isEqualTo(couponId) },
                { assertThat(issuedCoupon.userId).isEqualTo(userId) },
            )
        }
    }
}
