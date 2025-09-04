package com.loopers.interfaces.api.brand

import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.enums.brand.BrandStatusType
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
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.concurrent.CompletableFuture

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrandV1ApiE2ETest @Autowired constructor(
    private val testRestTemplate: TestRestTemplate,
    private val brandJpaRepository: BrandJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @MockitoBean
    lateinit var kafkaTemplate: KafkaTemplate<Any, Any>

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    companion object {
        private const val ENDPOINT_BRAND = "/api/v1/brands"
        private val ENDPOINT_BRAND_GET: (Long) -> String = { id: Long -> "/api/v1/brands/$id" }
    }

    /*
     **🌐 E2E 테스트**
    - [ ] 로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.
    - [ ] 브랜드 생성이 성공할 경우, 생성된 브랜드 정보를 응답으로 반환한다.
    - [ ] 브랜드명이 중복될 경우, 브랜드 등록에 실패한다.
     */
    @DisplayName("POST /api/v1/brands")
    @Nested
    inner class Create {

        @DisplayName("로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun throwsNotFoundException_whenUserDoesNotExist() {
            // arrange
            val nonExistentUsername = "nonExistentUser"
            val createRequest = BrandV1Dto.CreateRequest(
                "TestBrand",
            )
            val httpHeaders = HttpHeaders()
            httpHeaders.set("X-USER-ID", nonExistentUsername)
            val httpEntity = HttpEntity(createRequest, httpHeaders)

            // kafka mock
            val future = CompletableFuture.completedFuture(mock<SendResult<Any, Any>>())
            whenever(kafkaTemplate.send(any(), any(), any())).thenReturn(future)

            // act
            val response = testRestTemplate.exchange(
                ENDPOINT_BRAND,
                HttpMethod.POST,
                httpEntity,
                ApiResponse::class.java,
            )

            // assert
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }

        @DisplayName("브랜드 생성이 성공할 경우, 생성된 브랜드 정보를 응답으로 반환한다.")
        @Test
        fun returnsCreatedBrand_whenCreateBrandIsSuccessful() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createRequest = BrandV1Dto.CreateRequest(
                "TestBrand",
            )
            val httpHeaders = HttpHeaders()
            httpHeaders.set("X-USER-ID", createdUser.username)
            val httpEntity = HttpEntity<Any>(createRequest, httpHeaders)

            // kafka mock
            val future = CompletableFuture.completedFuture(mock<SendResult<Any, Any>>())
            whenever(kafkaTemplate.send(any(), any(), any())).thenReturn(future)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<BrandV1Dto.BrandResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_BRAND, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue() },
                { assertThat(response.body?.data?.name).isEqualTo(createRequest.name) },
                { assertThat(response.body?.data?.status).isEqualTo(BrandStatusType.ACTIVE) },
            )
        }

        @DisplayName("브랜드명이 중복될 경우, 브랜드 등록에 실패한다.")
        @Test
        fun throwsConflictException_whenBrandNameIsDuplicate() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createRequest = BrandV1Dto.CreateRequest(
                createdBrand.name,
            )
            val httpHeaders = HttpHeaders()
            httpHeaders.set("X-USER-ID", createdUser.username)
            val httpEntity = HttpEntity<Any>(createRequest, httpHeaders)

            // kafka mock
            val future = CompletableFuture.completedFuture(mock<SendResult<Any, Any>>())
            whenever(kafkaTemplate.send(any(), any(), any())).thenReturn(future)

            // act
            val response = testRestTemplate.exchange(
                ENDPOINT_BRAND,
                HttpMethod.POST,
                httpEntity,
                ApiResponse::class.java,
            )

            // assert
            assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        }
    }

    /*
     **🌐 E2E 테스트**
    - [ ] 존재하는 브랜드를 조회할 경우, 브랜드 정보를 응답으로 반환한다.
    - [ ] 존재하지 않는 브랜드를 조회할 경우, 404 Not Found 예외
     */
    @DisplayName("GET /api/v1/brands/{brandId}")
    @Nested
    inner class Get {
        @DisplayName("존재하는 브랜드를 조회할 경우, 브랜드 정보를 응답으로 반환한다.")
        @Test
        fun returnsBrand_whenGetBrandIsSuccessful() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val requestUrl = ENDPOINT_BRAND_GET(createdBrand.id)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<BrandV1Dto.BrandResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity<Any>(Unit), responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue() },
                { assertThat(response.body?.data?.id).isEqualTo(createdBrand.id) },
                { assertThat(response.body?.data?.name).isEqualTo(createdBrand.name) },
                { assertThat(response.body?.data?.status).isEqualTo(createdBrand.status) },
            )
        }

        @DisplayName("존재하지 않는 브랜드를 조회할 경우, 404 Not Found 예외가 발생한다.")
        @Test
        fun throwsNotFoundException_whenBrandDoesNotExist() {
            // arrange
            val nonExistentBrandId = 999L // 존재하지 않는 브랜드 ID
            val requestUrl = ENDPOINT_BRAND_GET(nonExistentBrandId)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<BrandV1Dto.BrandResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity<Any>(Unit), responseType)

            // assert
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }
    }
}
