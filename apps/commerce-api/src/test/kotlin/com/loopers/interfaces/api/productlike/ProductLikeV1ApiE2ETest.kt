package com.loopers.interfaces.api.productlike

import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.interfaces.api.example.ExampleV1Dto
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductLikeV1ApiE2ETest @Autowired constructor(
    private val testRestTemplate: TestRestTemplate,
    private val userJpaRepository: UserJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    companion object {
        private val ENDPOINT_LIKE: (Long) -> String = { productId: Long -> "/api/v1/like/products/$productId" }
        private val ENDPOINT_UNLIKE: (Long) -> String = { productId: Long -> "/api/v1/like/products/$productId" }
    }

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /**
     * 🌐 E2E 테스트
     * - [ ]  상품 좋아요를 누르면, 해당 상품에 대한 좋아요 정보가 생성된다.
     * - [ ]  상품 좋아요를 취소하면, 해당 상품에 대한 좋아요 정보가 삭제된다.
     */
    @DisplayName("POST /api/v1/like/products/{productId}")
    @Nested
    inner class Like {
        @DisplayName("상품 좋아요를 누르면, 해당 상품에 대한 좋아요 정보가 생성된다.")
        @Test
        fun createsProductLike_whenLikeIsSuccessful() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            val httpHeaders = HttpHeaders().apply {
                set("X-USER-ID", createdUser.username)
            }
            val httpEntity = HttpEntity<Any>(Unit, httpHeaders)
            val requestUrl = ENDPOINT_LIKE(createdProduct.id)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<Any>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertThat(response.statusCode.is2xxSuccessful)
        }
    }

    @DisplayName("DELETE /api/v1/like/products/{productId}")
    @Nested
    inner class Unlike {
        @DisplayName("상품 좋아요를 취소하면, 해당 상품에 대한 좋아요 정보가 삭제된다.")
        @Test
        fun deletesProductLike_whenUnlikeIsSuccessful() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            val httpHeaders = HttpHeaders().apply {
                set("X-USER-ID", createdUser.username)
            }
            val httpEntity = HttpEntity<Any>(Unit, httpHeaders)
            val requestUrl = ENDPOINT_UNLIKE(createdProduct.id)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<Any>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertThat(response.statusCode.is2xxSuccessful)
        }
    }

}
