package com.loopers.interfaces.api.point

import com.loopers.domain.point.PointEntityFixture.Companion.aPoint
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.infrastructure.point.PointJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.concurrent.CompletableFuture

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointV1ApiE2ETest @Autowired constructor(
    private val testRestTemplate: TestRestTemplate,
    private val userJpaRepository: UserJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @MockitoBean
    lateinit var kafkaTemplate: KafkaTemplate<Any, Any>

    @Autowired
    private lateinit var pointJpaRepository: PointJpaRepository

    companion object {
        private const val ENDPOINT_GET = "/api/v1/points"
        private const val ENDPOINT_CHARGE = "/api/v1/points/charge"
    }

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
     **🌐 E2E 테스트**

    - [ ]  포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.
    - [ ]  `X-USER-ID` 헤더가 없을 경우, `400 Bad Request` 응답을 반환한다.
     */
    @DisplayName("GET /api/v1/points")
    @Nested
    inner class Get {

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        fun returnsPoints_whenGetPointsIsSuccessful() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdPoint = pointJpaRepository.save(
                aPoint()
                    .userId(createdUser.id)
                    .build(),
            )
            val httpHeaders = HttpHeaders()
            httpHeaders.set("X-USER-ID", createdUser.username)
            val httpEntity = HttpEntity<Any>(Unit, httpHeaders)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResultResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_GET, HttpMethod.GET, httpEntity, responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue() },
                { assertThat(response.body?.data).isNotNull },
                { assertThat(response.body?.data?.userId).isEqualTo(createdUser.id) },
                { assertThat(response.body?.data?.point).isEqualTo(createdPoint.point.value) },
            )
        }

        @DisplayName("`X-USER-ID` 헤더가 없을 경우, `400 Bad Request` 응답을 반환한다.")
        @Test
        fun returnsBadRequest_whenUserIdHeaderIsMissing() {
            // arrange

            // kafka mock
            val future = CompletableFuture.completedFuture(mock<SendResult<Any, Any>>())
            whenever(kafkaTemplate.send(any(), any(), any())).thenReturn(future)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResultResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_GET, HttpMethod.GET, HttpEntity<Any>(Unit), responseType)

            // assert
            assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        }
    }

    /*
     **🌐 E2E 테스트**

    - [ ]  존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.
    - [ ]  존재하지 않는 유저로 요청할 경우, `404 Not Found` 응답을 반환한다.
     */
    @DisplayName("POST /api/v1/points/charge")
    @Nested
    inner class Charge {
        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        fun returnsChargedPoints_whenUserExistsAndCharges1000() {
            // arrange
            val point = 1000L
            val userEntity = userJpaRepository.save(aUser().build())
            val httpHeaders = HttpHeaders()
            httpHeaders.set("X-USER-ID", userEntity.username)
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            val requestBody = PointV1Dto.ChargeRequest(point)
            val httpEntity = HttpEntity(requestBody, httpHeaders)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResultResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_CHARGE, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue() },
                { assertThat(response.body?.data).isNotNull },
                { assertThat(response.body?.data?.userId).isEqualTo(userEntity.id) },
                { assertThat(response.body?.data?.point).isEqualTo(point) },
            )
        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        fun returnsNotFound_whenUserDoesNotExist() {
            // arrange
            val point = 1000L
            val httpHeaders = HttpHeaders()
            httpHeaders.set("X-USER-ID", "nonexistent-user")
            httpHeaders.contentType = MediaType.APPLICATION_JSON
            val requestBody = PointV1Dto.ChargeRequest(point)
            val httpEntity = HttpEntity(requestBody, httpHeaders)

            // kafka mock
            val future = CompletableFuture.completedFuture(mock<SendResult<Any, Any>>())
            whenever(kafkaTemplate.send(any(), any(), any())).thenReturn(future)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<PointV1Dto.PointResultResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_CHARGE, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }
    }
}
