package com.loopers.interfaces.api.coupon

import com.loopers.domain.coupon.fixture.CouponEntityFixture.Companion.aCoupon
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.infrastructure.coupon.CouponJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.enums.coupon.CouponStatusType
import com.loopers.support.enums.coupon.CouponType
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
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CouponV1ApiE2ETest @Autowired constructor(
    private val testRestTemplate: TestRestTemplate,
    private val userJpaRepository: UserJpaRepository,
    private val couponJpaRepository: CouponJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    companion object {
        private const val ENDPOINT_COUPON_CREATE = "/api/v1/coupons"
        private val ENDPOINT_COUPON_ISSUE: (Long) -> String = { couponId: Long -> "/api/v1/coupons/$couponId/issue" }
    }

    /*
    **🌐 E2E 테스트**
    - [ ]  쿠폰 생성이 성공할 경우, 생성된 쿠폰 정보를 응답으로 반환한다.
     */
    @DisplayName("POST /api/v1/coupons")
    @Nested
    inner class CreateCoupon {
        @DisplayName("쿠폰 생성이 성공할 경우, 생성된 쿠폰 정보를 응답으로 반환한다.")
        @Test
        fun returnsCreatedCoupon_whenCreateCouponIsSuccessful() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createRequest = CouponV1Dto.CreateRequest(
                "Test Coupon",
                CouponType.FIXED,
                1000L,
                10.0,
            )
            val httpHeaders = HttpHeaders().apply {
                set("X-USER-ID", createdUser.username)
            }
            val httpEntity = HttpEntity(createRequest, httpHeaders)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<CouponV1Dto.CouponDetail>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_COUPON_CREATE, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful) },
                { assertThat(response.body).isNotNull },
                { assertThat(response.body?.data?.name).isEqualTo(createRequest.couponName) },
                { assertThat(response.body?.data?.type).isEqualTo(createRequest.couponType) },
                { assertThat(response.body?.data?.discountAmount).isEqualTo(createRequest.discountAmount) },
                { assertThat(response.body?.data?.discountRate).isEqualTo(createRequest.discountRate) },
                { assertThat(response.body?.data?.status).isEqualTo(CouponStatusType.ACTIVE) },
            )
        }
    }

    /*
    **🌐 E2E 테스트**
    - [ ]  쿠폰 발급이 성공할 경우, 발급된 쿠폰 정보를 응답으로 반환한다.
     */
    @DisplayName("POST /api/v1/coupons/{couponId}/issue")
    @Nested
    inner class IssueCoupon {
        @DisplayName("쿠폰 발급이 성공할 경우, 발급된 쿠폰 정보를 응답으로 반환한다.")
        @Test
        fun returnsIssuedCoupon_whenIssueCouponIsSuccessful() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdCoupon = couponJpaRepository.save(aCoupon().build())
            val issueRequest = CouponV1Dto.IssueRequest(createdUser.id)
            val httpHeaders = HttpHeaders().apply {
                set("X-USER-ID", createdUser.username)
            }
            val httpEntity = HttpEntity(issueRequest, httpHeaders)
            val requestUrl = ENDPOINT_COUPON_ISSUE(createdCoupon.id)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<CouponV1Dto.IssuedCouponDetail>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful) },
                { assertThat(response.body).isNotNull },
                { assertThat(response.body?.data?.couponId).isEqualTo(createdCoupon.id) },
                { assertThat(response.body?.data?.userId).isEqualTo(createdUser.id) },
                { assertThat(response.body?.data?.status).isEqualTo(IssuedCouponStatusType.ACTIVE) },
                { assertThat(response.body?.data?.issuedAt).isNotNull },
                { assertThat(response.body?.data?.usedAt).isNull() },
            )
        }
    }

}
