package com.loopers.interfaces.api.user

import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.enums.user.GenderType
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
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ApiE2ETest @Autowired constructor(
    private val testRestTemplate: TestRestTemplate,
    private val userJpaRepository: UserJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    companion object {
        private const val ENDPOINT_POST = "/api/v1/users"
        private const val ENDPOINT_GET = "/api/v1/users/me"
    }

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /**
     **🌐 E2E 테스트**
    - [ ]  회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.
    - [ ]  회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.
     */
    @DisplayName("POST /api/v1/users")
    @Nested
    inner class SignUp {
        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        fun returnsUserInfo_whenSignUpIsSuccessful() {
            // arrange
            val signUpRequest = UserV1Dto.SignUpRequest(
                "user123",
                "soono",
                "soono@example.com",
                "1000-01-01",
                GenderType.M,
            )

            // act
            val userResponseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response = testRestTemplate.exchange(
                ENDPOINT_POST,
                HttpMethod.POST,
                HttpEntity<Any>(signUpRequest),
                userResponseType,
            )

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue() },
                { assertThat(response.body?.data).isNotNull() },
                { assertThat(response.body?.data?.userId).isEqualTo("user123") },
                { assertThat(response.body?.data?.name).isEqualTo("soono") },
                { assertThat(response.body?.data?.gender).isEqualTo(GenderType.M) },
            )
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.")
        @Test
        fun returnsBadRequest_whenGenderIsMissing() {
            // arrange
            val user = mapOf(
                "userId" to "user123",
                "name" to "soono",
                "email" to "soono@example.com",
                "birthday" to "1000-01-01",
            )
            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val httpEntity = HttpEntity(ObjectMapper().writeValueAsString(user), headers)

            // act
            val userResponseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response =
                testRestTemplate.exchange(ENDPOINT_POST, HttpMethod.POST, HttpEntity<Any>(httpEntity), userResponseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is4xxClientError).isTrue },
                { assertThat(response.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST) },
            )
        }
    }

    /*
     **🌐 E2E 테스트**

    - [ ]  내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.
    - [ ]  존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.
     */
    @DisplayName("POST /api/v1/users/me")
    @Nested
    inner class Get {
        @DisplayName("내 정보 조회에 성공할 경우, 해당하는 유저 정보를 응답으로 반환한다.")
        @Test
        fun returnsUserInfo_whenGetUserInfoIsSuccessful() {
            // arrange
            val userEntity = userJpaRepository.save(aUser().build())
            val httpHeaders = HttpHeaders()
            httpHeaders.set("X-USER-ID", userEntity.userId)
            val httpEntity = HttpEntity<Any>(Unit, httpHeaders)

            // act
            val userResponseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_GET, HttpMethod.GET, httpEntity, userResponseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue() },
                { assertThat(response.body?.data).isNotNull() },
                { assertThat(response.body?.data?.userId).isEqualTo(userEntity.userId) },
                { assertThat(response.body?.data?.name).isEqualTo(userEntity.name) },
            )
        }

        @DisplayName("존재하지 않는 ID 로 조회할 경우, `404 Not Found` 응답을 반환한다.")
        @Test
        fun returnsNotFound_whenUserDoesNotExist() {
            // arrange
            val unExistingUserId = "nonexistentUser"
            val httpHeaders = HttpHeaders()
            httpHeaders.set("X-USER-ID", unExistingUserId)
            val httpEntity = HttpEntity<Any>(Unit, httpHeaders)

            // act
            val userResponseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response =
                testRestTemplate.exchange(ENDPOINT_GET, HttpMethod.GET, httpEntity, userResponseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is4xxClientError).isTrue() },
                { assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND) },
            )
        }
    }
}
