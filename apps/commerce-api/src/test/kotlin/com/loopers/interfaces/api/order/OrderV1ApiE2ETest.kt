package com.loopers.interfaces.api.order

import com.loopers.domain.order.fixture.OrderEntityFixture.Companion.anOrder
import com.loopers.domain.point.PointEntityFixture.Companion.aPoint
import com.loopers.domain.point.vo.Point
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.stock.fixture.StockEntityFixture.Companion.aStock
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.infrastructure.order.OrderJpaRepository
import com.loopers.infrastructure.point.PointJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.stock.StockJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.KafkaMockConfig
import com.loopers.support.enums.payment.PaymentMethodType
import com.loopers.utils.DatabaseCleanUp
import com.loopers.utils.RedisCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@Import(KafkaMockConfig::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderV1ApiE2ETest @Autowired constructor(
    private val testRestTemplate: TestRestTemplate,
    private val orderJpaRepository: OrderJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
    private val stockJpaRepository: StockJpaRepository,
    private val pointJpaRepository: PointJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
    private val redisCleanUp: RedisCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
        redisCleanUp.truncateAll()
    }

    companion object {
        private const val ENDPOINT_ORDER = "/api/v1/orders"
        private val ENDPOINT_ORDER_GET: (Long) -> String = { id: Long -> "/api/v1/orders/$id" }
    }

    /*
     **🌐 E2E 테스트**
    - [ ] 로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.
    - [ ] 주문 ID가 존재하지 않으면 404 Not Found 예외가 발생
    - [ ] 주문 조회가 성공할 경우, 주문 상세 정보를 응답으로 반환한다.
     */
    @DisplayName("GET /api/v1/orders/{orderId}")
    @Nested
    inner class Get {
        @DisplayName("로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun throwsNotFoundException_whenUserDoesNotExist() {
            // arrange
            val nonExistentUsername = "nonExistentUser"
            val requestUrl = ENDPOINT_ORDER_GET(1L)
            val httpHeaders = HttpHeaders().apply { set("X-USER-ID", nonExistentUsername) }
            val httpEntity = HttpEntity<Any>(Unit, httpHeaders)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<OrderV1Dto.OrderResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, responseType)

            // assert
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }

        @DisplayName("주문 ID가 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun throwsNotFoundException_whenOrderDoesNotExist() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val nonExistentOrderId = 999L
            val requestUrl = ENDPOINT_ORDER_GET(nonExistentOrderId)
            val httpHeaders = HttpHeaders().apply { set("X-USER-ID", createdUser.username) }
            val httpEntity = HttpEntity<Any>(Unit, httpHeaders)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<OrderV1Dto.OrderResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, responseType)

            // assert
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }

        @DisplayName("주문 조회가 성공할 경우, 주문 상세 정보를 응답으로 반환한다.")
        @Test
        fun returnsOrderDetail_whenGetOrderIsSuccessful() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdOrder = orderJpaRepository.save(anOrder().build())
            val requestUrl = ENDPOINT_ORDER_GET(createdOrder.id)
            val httpHeaders = HttpHeaders().apply { set("X-USER-ID", createdUser.username) }
            val httpEntity = HttpEntity<Any>(Unit, httpHeaders)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<OrderV1Dto.OrderResponse>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, responseType)

            // assert
            assertAll(
                { assertThat(response.statusCode.is2xxSuccessful).isTrue() },
                { assertThat(response.body?.data?.orderId).isEqualTo(createdOrder.id) },
                { assertThat(response.body?.data?.ordererName).isEqualTo(createdOrder.orderCustomer.name) },
                { assertThat(response.body?.data?.ordererEmail).isEqualTo(createdOrder.orderCustomer.email.value) },
                { assertThat(response.body?.data?.ordererMobile).isEqualTo(createdOrder.orderCustomer.mobile.value) },
                { assertThat(response.body?.data?.ordererZipCode).isEqualTo(createdOrder.orderCustomer.address.zipCode) },
                { assertThat(response.body?.data?.ordererAddress).isEqualTo(createdOrder.orderCustomer.address.address) },
                { assertThat(response.body?.data?.ordererAddressDetail).isEqualTo(createdOrder.orderCustomer.address.addressDetail) },
            )
        }
    }

    /*
     **🌐 E2E 테스트**
    - [ ] 로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.
    - [ ] 결제 타입[포인트] 주문이 성공하면, 주문 ID를 응답으로 반환한다.
     */
    @DisplayName("POST /api/v1/orders")
    @Nested
    inner class Create {
        @DisplayName("로그인 사용자가 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun throwsNotFoundException_whenUserDoesNotExist() {
            // arrange
            val nonExistentUsername = "nonExistentUser"
            val requestUrl = ENDPOINT_ORDER
            val httpHeaders = HttpHeaders().apply { set("X-USER-ID", nonExistentUsername) }
            val orderCreateRequest = OrderV1Dto.CreateRequest(
                "soono",
                "shyoon991@gmail.com",
                "010-1234-5678",
                "12345",
                "서울시 강남구 테헤란로 123",
                "테헤란로 123",
                listOf(
                    OrderV1Dto.CreateRequest.OrderItemRequest(
                        1L,
                        1,
                    ),
                ),
                PaymentMethodType.POINT,
            )
            val httpEntity = HttpEntity<Any>(orderCreateRequest, httpHeaders)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<Long>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }

        @DisplayName("결제 타입[포인트] 주문이 성공적으로 생성되면, 주문 ID를 응답으로 반환한다.")
        @Test
        fun returnsOrderId_whenCreateOrderIsSuccessful() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            pointJpaRepository.save(aPoint().userId(createdUser.id).point(Point(10000)).build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            stockJpaRepository.save(aStock().productId(createdProduct.id).quantity(10).build())
            val orderCreateRequest = OrderV1Dto.CreateRequest(
                "soono",
                "shyoon991@gmail.com",
                "010-1234-5678",
                "12345",
                "서울시 강남구 테헤란로 123",
                "테헤란로 123",
                listOf(
                    OrderV1Dto.CreateRequest.OrderItemRequest(
                        createdProduct.id,
                        1,
                    ),
                ),
                PaymentMethodType.POINT,
            )
            val requestUrl = ENDPOINT_ORDER
            val httpHeaders = HttpHeaders().apply { set("X-USER-ID", createdUser.username) }
            val httpEntity = HttpEntity<Any>(orderCreateRequest, httpHeaders)

            // act
            val responseType = object : ParameterizedTypeReference<ApiResponse<Long>>() {}
            val response = testRestTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, responseType)

            // assert
            assertThat(response.statusCode.is2xxSuccessful).isTrue()
            assertThat(response.body?.data).isNotNull()
        }
    }
}
