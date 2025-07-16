package com.loopers.interfaces.api.user

import com.loopers.interfaces.api.ApiResponse
import org.assertj.core.api.Assertions.assertThat
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
) {

    companion object {
        private const val ENDPOINT_POST = "/api/v1/users"
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
                userId = "user123",
                name = "soono",
                email = "soono@example.com",
                birthday = "1000-01-01",
                gender = UserV1Dto.SignUpRequest.GenderRequest.M,
            )

            // act
            val userResponseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_POST, HttpMethod.POST, HttpEntity<Any>(signUpRequest), userResponseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue() },
                { assertThat(response.body?.data).isNotNull() },
                { assertThat(response.body?.data?.userId).isEqualTo("user123") },
                { assertThat(response.body?.data?.name).isEqualTo("soono") },
                { assertThat(response.body?.data?.gender).isEqualTo(UserV1Dto.UserResponse.GenderResponse.M) },
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
            val response = testRestTemplate.exchange(ENDPOINT_POST, HttpMethod.POST, HttpEntity<Any>(httpEntity), userResponseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is4xxClientError).isTrue },
                { assertThat(response.body?.meta?.result).isEqualTo(ApiResponse.Metadata.Result.FAIL) },
                { assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST) },
            )
        }
    }
}
