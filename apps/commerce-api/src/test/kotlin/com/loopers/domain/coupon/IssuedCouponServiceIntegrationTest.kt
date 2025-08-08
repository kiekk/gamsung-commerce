package com.loopers.domain.coupon

import com.loopers.domain.coupon.fixture.IssuedCouponEntityFixture.Companion.anIssuedCoupon
import com.loopers.infrastructure.coupon.IssuedCouponJpaRepository
import com.loopers.support.enums.coupon.IssuedCouponStatusType
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
class IssuedCouponServiceIntegrationTest @Autowired constructor(
    private val issuedCouponService: IssuedCouponService,
    private val issuedCouponJpaRepository: IssuedCouponJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
     **🔗 통합 테스트
    - [ ] 쿠폰을 발급하면 발급된 쿠폰이 저장된다.
    - [ ] 발급된 쿠폰을 조회하면 해당 쿠폰 정보를 반환한다.
     */
    @DisplayName("쿠폰을 발급할 때, ")
    @Nested
    inner class Issue {
        @DisplayName("쿠폰을 발급하면 발급된 쿠폰이 저장된다.")
        @Test
        fun savesIssuedCoupon_whenCouponIsIssued() {
            // arrange
            val command = IssuedCouponCommand.Issue(
                1L,
                1L,
            )

            // act
            val createdIssuedCoupon = issuedCouponService.issueCoupon(command)

            // assert
            assertAll(
                { assertThat(createdIssuedCoupon.couponId).isEqualTo(command.couponId) },
                { assertThat(createdIssuedCoupon.userId).isEqualTo(command.userId) },
                { assertThat(createdIssuedCoupon.status).isEqualTo(IssuedCouponStatusType.ACTIVE) },
            )
        }

        @DisplayName("발급된 쿠폰을 조회하면 해당 쿠폰 정보를 반환한다.")
        @Test
        fun returnsIssuedCoupon_whenIssuedCouponExists() {
            // arrange
            val createdIssuedCoupon = issuedCouponJpaRepository.save(anIssuedCoupon().build())

            // act
            val findIssuedCoupon = issuedCouponService.findIssuedCouponById(createdIssuedCoupon.id)

            // assert
            assertAll(
                { assertThat(findIssuedCoupon?.couponId).isEqualTo(createdIssuedCoupon.couponId) },
                { assertThat(findIssuedCoupon?.userId).isEqualTo(createdIssuedCoupon.userId) },
                { assertThat(findIssuedCoupon?.status).isEqualTo(createdIssuedCoupon.status) },
            )
        }
    }
}
