package com.loopers.application.coupon

import com.loopers.domain.coupon.fixture.CouponEntityFixture.Companion.aCoupon
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.domain.vo.PercentRate
import com.loopers.domain.vo.Price
import com.loopers.infrastructure.coupon.CouponJpaRepository
import com.loopers.infrastructure.coupon.IssuedCouponJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.enums.coupon.CouponType
import com.loopers.support.error.CoreException
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CouponFacadeIntegrationTest @Autowired constructor(
    private val couponFacade: CouponFacade,
    private val userJpaRepository: UserJpaRepository,
    private val issuedCouponJpaRepository: IssuedCouponJpaRepository,
    private val couponJpaRepository: CouponJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
    **🔗 통합 테스트
    - [ ] 로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.
    - [ ] 쿠폰명이 중복되면 409 Conflict 예외가 발생한다.
    - [ ] 쿠폰이 생성되면 쿠폰 정보가 반환된다.
     */
    @DisplayName("쿠폰을 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun throwsNotFoundException_whenUserDoesNotExist() {
            // arrange
            val nonExistentUsername = "nonExistentUser"
            val criteria = CouponCriteria.Create(
                nonExistentUsername,
                "Coupon Name",
                CouponType.FIXED,
                Price(1000L),
                PercentRate(10.0),
            )

            // act
            val exception = assertThrows<CoreException> {
                couponFacade.createCoupon(criteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).contains("사용자를 찾을 수 없습니다. username: $nonExistentUsername") },
            )
        }

        @DisplayName("쿠폰명이 중복되면 409 Conflict 예외가 발생한다.")
        @Test
        fun throwsConflictException_whenCouponNameIsDuplicate() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdCoupon = couponJpaRepository.save(aCoupon().name("Duplicate Coupon").build())
            val criteria = CouponCriteria.Create(
                createdUser.username,
                createdCoupon.name,
                CouponType.FIXED,
                Price(1000L),
                PercentRate(10.0),
            )

            // act
            val exception = assertThrows<CoreException> {
                couponFacade.createCoupon(criteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).contains("이미 존재하는 쿠폰명입니다. 쿠폰명: ${createdCoupon.name}") },
            )
        }

        @DisplayName("쿠폰이 생성되면 쿠폰 정보가 반환된다.")
        @Test
        fun returnsCreatedCoupon_whenCouponIsCreated() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val criteria = CouponCriteria.Create(
                createdUser.username,
                "Coupon Name",
                CouponType.FIXED,
                Price(1000L),
                PercentRate(10.0),
            )

            // act
            val createdCoupon = couponFacade.createCoupon(criteria)

            // assert
            val findCoupon = couponJpaRepository.findById(createdCoupon.id)
            assertAll(
                { assertThat(findCoupon.get().name).isEqualTo(criteria.name) },
                { assertThat(findCoupon.get().type).isEqualTo(criteria.type) },
                { assertThat(findCoupon.get().discountAmount).isEqualTo(criteria.discountAmount) },
                { assertThat(findCoupon.get().discountRate).isEqualTo(criteria.discountRate) },
                { assertThat(findCoupon.get().status).isEqualTo(createdCoupon.status) },
            )
        }
    }

    /*
    **🔗 통합 테스트
    - [ ] 로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.
    - [ ] 쿠폰이 존재하지 않으면 404 Not Found 예외가 발생한다.
    - [ ] 쿠폰이 발급 가능한 상태가 아니면 409 Conflict 예외가 발생한다.
    - [ ] 발급할 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.
    - [ ] 쿠폰을 발급하면 발급된 쿠폰 정보가 반환된다.
     */
    @DisplayName("쿠폰을 발급할 때, ")
    @Nested
    inner class Issue {
        @DisplayName("로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun throwsNotFoundException_whenUserDoesNotExist() {
            // arrange
            val nonExistentUsername = "nonExistentUser"
            val createdCoupon = couponJpaRepository.save(aCoupon().build())
            val createdUser = userJpaRepository.save(aUser().build())
            val criteria = CouponCriteria.Issue(
                nonExistentUsername,
                createdUser.id,
                createdCoupon.id,
            )

            // act
            val exception = assertThrows<CoreException> {
                couponFacade.issueCoupon(criteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).contains("사용자를 찾을 수 없습니다. username: $nonExistentUsername") },
            )
        }

        @DisplayName("쿠폰이 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun throwsNotFoundException_whenCouponDoesNotExist() {
            // arrange
            val nonExistentCouponId = 999L // 존재하지 않는 쿠폰 ID
            val createdUser = userJpaRepository.save(aUser().build())
            val criteria = CouponCriteria.Issue(
                createdUser.username,
                createdUser.id,
                nonExistentCouponId,
            )

            // act
            val exception = assertThrows<CoreException> {
                couponFacade.issueCoupon(criteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).contains("쿠폰을 찾을 수 없습니다. couponId: $nonExistentCouponId") },
            )
        }

        @DisplayName("쿠폰이 발급 가능한 상태가 아니면 409 Conflict 예외가 발생한다.")
        @Test
        fun throwsConflictException_whenCouponIsNotAvailable() {
            // arrange
            val createdCoupon = couponJpaRepository.save(aCoupon().build().apply { inactive() })
            val createdUser = userJpaRepository.save(aUser().build())
            val criteria = CouponCriteria.Issue(
                createdUser.username,
                createdUser.id,
                createdCoupon.id,
            )

            // act
            val exception = assertThrows<CoreException> {
                couponFacade.issueCoupon(criteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).contains("발급 가능한 쿠폰이 아닙니다. 쿠폰 상태: ${createdCoupon.status}, couponId: ${createdCoupon.id}") },
            )
        }

        @DisplayName("발급할 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun throwsNotFoundException_whenUserToIssueDoesNotExist() {
            // arrange
            val createdCoupon = couponJpaRepository.save(aCoupon().build())
            val nonExistentUserId = 999L // 존재하지 않는 사용자 ID
            val createdUser = userJpaRepository.save(aUser().build())
            val criteria = CouponCriteria.Issue(
                createdUser.username,
                nonExistentUserId,
                createdCoupon.id,
            )

            // act
            val exception = assertThrows<CoreException> {
                couponFacade.issueCoupon(criteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).contains("쿠폰을 발급할 사용자를 찾을 수 없습니다. userId: $nonExistentUserId") },
            )
        }

        @DisplayName("쿠폰을 발급하면 발급된 쿠폰 정보가 반환된다.")
        @Test
        fun returnsIssuedCoupon_whenCouponIsIssuedSuccessfully() {
            // arrange
            val createdCoupon = couponJpaRepository.save(aCoupon().build())
            val createdUser = userJpaRepository.save(aUser().build())
            val criteria = CouponCriteria.Issue(
                createdUser.username,
                createdUser.id,
                createdCoupon.id,
            )

            // act
            val createdIssuedCoupon = couponFacade.issueCoupon(criteria)

            // assert
            val findIssuedCoupon = issuedCouponJpaRepository.findById(createdIssuedCoupon.id)
            assertAll(
                { assertThat(findIssuedCoupon).isPresent },
                { assertThat(findIssuedCoupon.get().couponId).isEqualTo(createdCoupon.id) },
                { assertThat(findIssuedCoupon.get().userId).isEqualTo(createdUser.id) },
                { assertThat(findIssuedCoupon.get().status).isEqualTo(createdIssuedCoupon.status) },
            )
        }
    }

}
