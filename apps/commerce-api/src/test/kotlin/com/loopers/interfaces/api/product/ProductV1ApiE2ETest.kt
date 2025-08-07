package com.loopers.interfaces.api.product

import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.enums.product.ProductStatusType
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductV1ApiE2ETest @Autowired constructor(
    private val testRestTemplate: TestRestTemplate,
    private val brandJpaRepository: BrandJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    companion object {
        private const val ENDPOINT_PRODUCT = "/api/v1/products"
        private val ENDPOINT_PRODUCT_GET: (Long) -> String = { id: Long -> "/api/v1/products/$id" }
    }

    /*
     **🌐 E2E 테스트**
    - [ ] 로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.
    - [ ] 상품 생성이 성공할 경우, 생성된 상품 정보를 응답으로 반환한다.
    - [ ] 상품명이 중복될 경우, 상품 등록에 실패한다.
     */
    @DisplayName("POST /api/v1/products")
    @Nested
    inner class Create {
        @DisplayName("로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun throwsNotFoundException_whenUserDoesNotExist() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val nonExistentUsername = "nonExistentUser"
            val createRequest = ProductV1Dto.CreateRequest(
                createdBrand.id,
                "TestProduct",
                1000,
                "Test Description",
                ProductStatusType.ACTIVE,
            )
            val httpHeaders = HttpHeaders().apply { set("X-USER-ID", nonExistentUsername) }
            val httpEntity = HttpEntity<Any>(createRequest, httpHeaders)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_PRODUCT, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }

        @DisplayName("상품 생성이 성공할 경우, 생성된 상품 정보를 응답으로 반환한다.")
        @Test
        fun returnsCreatedProduct_whenCreationIsSuccessful() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdUser = userJpaRepository.save(aUser().build())
            val createRequest = ProductV1Dto.CreateRequest(
                createdBrand.id,
                "TestProduct",
                1000,
                "Test Description",
                ProductStatusType.ACTIVE,
            )
            val httpHeaders = HttpHeaders().apply { set("X-USER-ID", createdUser.username) }
            val httpEntity = HttpEntity<Any>(createRequest, httpHeaders)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_PRODUCT, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful) },
                { assertThat(response.body?.data).isNotNull() },
                { assertThat(response.body?.data?.name).isEqualTo(createRequest.name) },
                { assertThat(response.body?.data?.description).isEqualTo(createRequest.description) },
                { assertThat(response.body?.data?.price).isEqualTo(createRequest.price) },
                { assertThat(response.body?.data?.status).isEqualTo(createRequest.status) },
            )
        }

        @DisplayName("상품명이 중복될 경우, 상품 등록에 실패한다.")
        @Test
        fun throwsConflictException_whenProductNameIsDuplicate() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            val createRequest = ProductV1Dto.CreateRequest(
                createdBrand.id,
                createdProduct.name,
                1000,
                "Test Description",
                ProductStatusType.ACTIVE,
            )
            val httpHeaders = HttpHeaders().apply { set("X-USER-ID", createdUser.username) }
            val httpEntity = HttpEntity<Any>(createRequest, httpHeaders)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse>>() {}
            val response = testRestTemplate.exchange(ENDPOINT_PRODUCT, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
        }
    }

    /*
     **🌐 E2E 테스트**
    - [ ] 상품 ID로 상품 조회 시, 해당 상품이 존재하지 않으면 404 Not Found 예외가 발생한다.
    - [ ] 상품 ID로 상품 조회 시, 해당 상품이 존재하면 상품 정보를 응답으로 반환한다.
     */
    @DisplayName("GET /api/v1/products/{id}")
    @Nested
    inner class Get {
        @DisplayName("상품 ID로 상품 조회 시, 해당 상품이 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun throwsNotFoundException_whenProductDoesNotExist() {
            // arrange
            val nonExistentProductId = 999L
            val requestUrl = ENDPOINT_PRODUCT_GET(nonExistentProductId)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity<Any>(Unit), responseType)

            // assert
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }

        @DisplayName("상품 ID로 상품 조회 시, 해당 상품이 존재하면 상품 정보를 응답으로 반환한다.")
        @Test
        fun returnsProduct_whenGetProductIsSuccessful() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val requestUrl = ENDPOINT_PRODUCT_GET(createdProduct.id)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<ProductV1Dto.ProductDetailResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.GET, null, responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful) },
                { assertThat(response.body?.data?.id).isEqualTo(createdProduct.id) },
                { assertThat(response.body?.data?.productName).isEqualTo(createdProduct.name) },
                { assertThat(response.body?.data?.brandName).isEqualTo(createdBrand.name) },
                { assertThat(response.body?.data?.productStatus).isEqualTo(createdProduct.status) },
                { assertThat(response.body?.data?.productPrice).isEqualTo(createdProduct.price.value) },
                { assertThat(response.body?.data?.productLikeCount).isEqualTo(0) }, // 초기 좋아요 수는 0으로 가정
            )
        }
    }
}
