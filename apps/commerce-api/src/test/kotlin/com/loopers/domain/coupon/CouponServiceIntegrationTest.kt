package com.loopers.domain.coupon

import com.loopers.domain.vo.PercentRate
import com.loopers.domain.vo.Price
import com.loopers.infrastructure.coupon.CouponJpaRepository
import com.loopers.support.enums.coupon.CouponType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CouponServiceIntegrationTest @Autowired constructor(
    private val couponService: CouponService,
    private val couponJpaRepository: CouponJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
     **🔗 통합 테스트
    - [ ] 쿠폰을 생성하면 쿠폰이 저장된다.
     */
    @DisplayName("쿠폰을 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("쿠폰을 생성하면 쿠폰이 저장된다.")
        @Test
        fun savesCoupon_whenCouponIsCreated() {
            // arrange
            val couponCommand = CouponCommand.Create(
                "10% 할인 쿠폰",
                CouponType.FIXED,
                Price(10_000L),
                PercentRate(10.0),
            )

            // act
            val createdCoupon = couponService.createCoupon(couponCommand)

            // assert
            assertAll(
                { assertThat(couponJpaRepository.findAll()).hasSize(1) },
                { assertThat(createdCoupon).isNotNull },
                { assertThat(createdCoupon.name).isEqualTo(couponCommand.name) },
                { assertThat(createdCoupon.discountRate).isEqualTo(couponCommand.discountRate) },
                { assertThat(createdCoupon.discountAmount).isEqualTo(couponCommand.discountAmount) },
            )
        }
    }
}
