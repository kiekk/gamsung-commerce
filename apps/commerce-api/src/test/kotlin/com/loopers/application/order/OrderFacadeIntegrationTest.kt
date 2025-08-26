package com.loopers.application.order

import com.loopers.domain.coupon.fixture.CouponEntityFixture.Companion.aCoupon
import com.loopers.domain.coupon.fixture.IssuedCouponEntityFixture.Companion.anIssuedCoupon
import com.loopers.domain.order.fixture.OrderEntityFixture.Companion.anOrder
import com.loopers.domain.order.fixture.OrderItemEntityFixture.Companion.anOrderItem
import com.loopers.domain.order.vo.OrderCustomerFixture.Companion.anOrderCustomer
import com.loopers.domain.payment.gateway.PaymentGateway
import com.loopers.domain.payment.gateway.PaymentGatewayResult
import com.loopers.domain.point.PointEntityFixture.Companion.aPoint
import com.loopers.domain.point.vo.Point
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.stock.fixture.StockEntityFixture.Companion.aStock
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.domain.vo.Address
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Mobile
import com.loopers.domain.vo.Price
import com.loopers.domain.vo.Quantity
import com.loopers.event.payload.order.OrderCreatedEvent
import com.loopers.event.payload.payment.PaymentCompletedEvent
import com.loopers.infrastructure.coupon.CouponJpaRepository
import com.loopers.infrastructure.coupon.IssuedCouponJpaRepository
import com.loopers.infrastructure.order.OrderJpaRepository
import com.loopers.infrastructure.payment.PaymentJpaRepository
import com.loopers.infrastructure.point.PointJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.stock.StockJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.enums.coupon.IssuedCouponStatusType
import com.loopers.support.enums.order.OrderStatusType
import com.loopers.support.enums.payment.PaymentCardType
import com.loopers.support.enums.payment.PaymentMethodType
import com.loopers.support.enums.payment.PaymentStatusType
import com.loopers.support.error.CoreException
import com.loopers.utils.DatabaseCleanUp
import com.loopers.utils.RedisCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.event.ApplicationEvents
import org.springframework.test.context.event.RecordApplicationEvents
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@RecordApplicationEvents
@SpringBootTest
class OrderFacadeIntegrationTest @Autowired constructor(
    private val userJpaRepository: UserJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
    private val stockJpaRepository: StockJpaRepository,
    private val pointJpaRepository: PointJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
    private val orderJpaRepository: OrderJpaRepository,
    private val couponJpaRepository: CouponJpaRepository,
    private val issuedCouponJpaRepository: IssuedCouponJpaRepository,
    private val paymentJpaRepository: PaymentJpaRepository,
    private val redisCleanUp: RedisCleanUp,
) {

    @Autowired
    lateinit var applicationEvents: ApplicationEvents

    @MockitoBean
    lateinit var paymentGateway: PaymentGateway

    @Autowired
    lateinit var orderFacade: OrderFacade

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
        redisCleanUp.truncateAll()
        applicationEvents.clear()
    }

    /*
     **🔗 통합 테스트
    - [ ] 존재하지 않는 사용자가 주문을 요청할 경우 예외가 발생한다.
    - [ ] 주문 항목의 productId에 해당하는 상품이 존재하지 않으면 예외가 발생한다.
    - [ ] 주문 항목의 productId에 해당하는 상품이 주문 가능한 상태가 아니면 예외가 발생한다.
    - [ ] 주문 항목의 수량이 상품의 재고를 초과하면 예외가 발생한다.
    - [ ] 쿠폰 적용 시, 쿠폰이 존재하지 않으면 404 Not Found 예외가 발생한다.
    - [ ] 쿠폰 적용 시, 쿠폰이 이미 사용한 상태라면 409 Conflict 예외가 발생한다.
    - [ ] 쿠폰 적용 시, 쿠폰이 유효하면 쿠폰 할인 금액만큼 주문 금액이 할인된다.
     */
    @DisplayName("결제 타입[포인트]으로 주문을 생성할 때, ")
    @Nested
    inner class CreateByPointType {

        @DisplayName("존재하지 않는 사용자가 주문을 요청할 경우 예외가 발생한다.")
        @Test
        fun failsToCreateOrder_whenUserDoesNotExist() {
            // arrange
            val nonExistentUsername = "nonExistentUser"
            val createdProduct = productJpaRepository.save(aProduct().build())
            val orderCriteria = OrderCriteria.Create(
                nonExistentUsername,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        Quantity(2),
                    ),
                ),
                PaymentMethodType.POINT,
            )

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.placeOrder(orderCriteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("사용자를 찾을 수 없습니다. username: $nonExistentUsername") },
            )
        }

        @DisplayName("주문 항목의 productId에 해당하는 상품이 존재하지 않으면 예외가 발생한다.")
        @Test
        fun failsToCreateOrder_whenProductDoesNotExist() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val nonExistentProductId = 999L
            val orderCriteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        nonExistentProductId,
                        Quantity(2),
                    ),
                ),
                PaymentMethodType.POINT,
            )

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.placeOrder(orderCriteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("존재하지 않는 상품입니다. productId: $nonExistentProductId") },
            )
        }

        @DisplayName("주문 항목의 productId에 해당하는 상품이 주문 가능한 상태가 아니면 예외가 발생한다.")
        @Test
        fun failsToCreateOrder_whenProductIsNotAvailable() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build().apply { inactive() })
            val orderCriteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        Quantity(2),
                    ),
                ),
                PaymentMethodType.POINT,
            )

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.placeOrder(orderCriteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("주문 가능한 상태가 아닌 상품입니다. productId: ${createdProduct.id}, 상태: ${createdProduct.status}") },
            )
        }

        @DisplayName("주문 항목의 수량이 상품의 재고를 초과하면 예외가 발생한다.")
        @Test
        fun failsToCreateOrder_whenQuantityExceedsStock() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            val createdStock = stockJpaRepository.save(aStock().quantity(0).build())
            val quantity = Quantity(2)
            val orderCriteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        quantity,
                    ),
                ),
                PaymentMethodType.POINT,
            )

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.placeOrder(orderCriteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("재고가 부족한 상품입니다. productId: ${createdProduct.id}, 요청 수량: ${quantity.value}, 재고: ${createdStock.quantity}") },
            )
        }

        @DisplayName("쿠폰 적용 시, 쿠폰이 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun failsToCreateOrder_whenCouponDoesNotExist() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            stockJpaRepository.save(aStock().build())
            val nonExistIssuedCouponId: Long = 999L
            val quantity = Quantity(2)
            val orderCriteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        quantity,
                    ),
                ),
                PaymentMethodType.POINT,
                nonExistIssuedCouponId,
            )

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.placeOrder(orderCriteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("존재하지 않는 사용자 쿠폰입니다. issuedCouponId: $nonExistIssuedCouponId") },
            )
        }

        @DisplayName("쿠폰 적용 시, 쿠폰이 이미 사용한 상태라면 409 Conflict 예외가 발생한다.")
        @Test
        fun failsToCreateOrder_whenCouponAlreadyUsed() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            stockJpaRepository.save(aStock().build())
            val quantity = Quantity(2)
            val createdCoupon = couponJpaRepository.save(aCoupon().build())
            val createdIssuedCoupon = issuedCouponJpaRepository.save(
                anIssuedCoupon()
                    .couponId(createdCoupon.id)
                    .userId(createdUser.id)
                    .build().apply { use() },
            )
            val orderCriteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        quantity,
                    ),
                ),
                PaymentMethodType.POINT,
                createdIssuedCoupon.id,
            )

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.placeOrder(orderCriteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("이미 사용한 사용자 쿠폰입니다. issuedCouponId: ${createdIssuedCoupon.id}, 상태: ${createdIssuedCoupon.status}") },
            )
        }

        @DisplayName("쿠폰 적용 시, 쿠폰이 유효하면 쿠폰 할인 금액만큼 주문 금액이 할인된다.")
        @Test
        fun succeedsToCreateOrder_whenCouponIsValid() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            pointJpaRepository.save(aPoint().userId(createdUser.id).point(Point(10_000L)).build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            stockJpaRepository.save(aStock().build())
            val quantity = Quantity(2)
            val createdCoupon = couponJpaRepository.save(aCoupon().build())
            val createdIssuedCoupon = issuedCouponJpaRepository.save(anIssuedCoupon().couponId(createdCoupon.id).userId(createdUser.id).build())
            val orderCriteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        quantity,
                    ),
                ),
                PaymentMethodType.POINT,
                createdIssuedCoupon.id,
            )

            // act
            val orderId = orderFacade.placeOrder(orderCriteria)

            // assert
            await().atMost(Duration.ofSeconds(2)).untilAsserted {
                val findOrder = orderJpaRepository.findWithItemsById(orderId)
                findOrder?.let { order ->
                    assertAll(
                        { assertThat(order.orderStatus).isEqualTo(OrderStatusType.COMPLETED) },
                        { assertThat(order.amount).isEqualTo(Price(order.totalPrice.value - order.discountPrice.value)) },
                    )
                }
            }
        }
    }

    /*
     **🔗 통합 테스트
    - [ ] 포인트로 결제에 성공하면 재고가 감소하며 결제 성공, 주문 완료 처리 된다.
    - [ ] 포인트 정보가 없을 경우 예외가 발생하고 주문 정보는 생성되지 않는다.
    - [ ] 포인트 부족 시 예외가 발생하고 주문 정보는 생성되지 않는다.
     */
    @DisplayName("주문을 결제할 때, ")
    @Nested
    inner class Payment {
        @DisplayName("포인트로 결제에 성공하면 재고가 감소하며 결제 성공, 주문 완료 처리 된다.")
        @Test
        fun succeedsToPayWithPoints_whenPaymentIsSuccessful() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdPoint = pointJpaRepository.save(aPoint().userId(createdUser.id).point(Point(20_000)).build())
            val createdProduct = productJpaRepository.save(aProduct().price(Price(1000)).build())
            stockJpaRepository.save(aStock().build())
            val quantity = Quantity(2)
            val criteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        quantity,
                    ),
                ),
                PaymentMethodType.POINT,
            )

            // act
            val orderId = orderFacade.placeOrder(criteria)

            // assert
            await().atMost(Duration.ofSeconds(2)).untilAsserted {
                val findOrder = orderJpaRepository.findWithItemsById(orderId)
                findOrder?.let { order ->
                    assertAll(
                        { assertThat(order.userId).isEqualTo(createdUser.id) },
                        { assertThat(order.orderStatus).isEqualTo(OrderStatusType.COMPLETED) },
                        { assertThat(order.orderItems.size()).isEqualTo(2) },
                        { assertThat(order.orderItems.amount()).isEqualTo(Price(createdProduct.price.value * quantity.value)) },
                    )
                }
                val findPoint = pointJpaRepository.findByUserId(createdUser.id)
                assertThat(findPoint?.point).isEqualTo(Point(createdPoint.point.value - (createdProduct.price.value * quantity.value)))
            }
        }

        @DisplayName("포인트 정보가 없을 경우 예외가 발생하고 주문 상태는 PENDING이다.")
        @Test
        fun failsToPayWithPoints_whenPointDoesNotExist() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().price(Price(1000)).build())
            stockJpaRepository.save(aStock().build())
            val quantity = Quantity(2)
            val criteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        quantity,
                    ),
                ),
                PaymentMethodType.POINT,
            )

            // act
            val orderId = orderFacade.placeOrder(criteria)

            // assert
            await().atMost(Duration.ofSeconds(2)).untilAsserted {
                val findOrder = orderJpaRepository.findWithItemsById(orderId)
                val callOrderCreatedEventCount = applicationEvents.stream(OrderCreatedEvent::class.java)
                    .filter { event -> event.orderId == orderId }
                    .count()
                val callPaymentCompletedEventCount = applicationEvents.stream(PaymentCompletedEvent::class.java)
                    .filter { event -> event.orderKey == findOrder?.orderKey }
                    .count()

                assertAll(
                    { assertThat(callOrderCreatedEventCount).isEqualTo(1) },
                    { assertThat(callPaymentCompletedEventCount).isZero },
                    { assertThat(findOrder?.orderStatus).isEqualTo(OrderStatusType.PENDING) },
                )
            }
        }

        @DisplayName("포인트 부족 시 예외가 발생하고 주문은 PENDING 상태가 된다.")
        @Test
        fun failsToPayWithPoints_whenPointIsInsufficient() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            pointJpaRepository.save(aPoint().userId(createdUser.id).point(Point(1000)).build())
            val createdProduct = productJpaRepository.save(aProduct().price(Price(1000)).build())
            stockJpaRepository.save(aStock().build())
            val quantity = Quantity(2)
            val criteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        quantity,
                    ),
                ),
                PaymentMethodType.POINT,
            )

            // act
            val orderId = orderFacade.placeOrder(criteria)

            // assert
            val findOrder = orderJpaRepository.findWithItemsById(orderId)
            val callOrderCreatedEventCount = applicationEvents.stream(OrderCreatedEvent::class.java)
                .filter { event -> event.orderId == orderId }
                .count()
            val callPaymentCompletedEventCount = applicationEvents.stream(PaymentCompletedEvent::class.java)
                .filter { event -> event.orderKey == findOrder?.orderKey }
                .count()
            assertAll(
                { assertThat(callOrderCreatedEventCount).isEqualTo(1) },
                { assertThat(callPaymentCompletedEventCount).isZero },
                { assertThat(findOrder?.orderStatus).isEqualTo(OrderStatusType.PENDING) },
            )
        }
    }

    /*
     **🔗 통합 테스트
     - [ ] 사용자 정보가 존재하지 않으면 404 Not Found 예외가 발생한다.
     - [ ] 주문 상세 조회 시 주문이 존재하지 않으면 404 Not Found 예외가 발생한다.
     - [ ] 주문 상세 조회 시 주문이 존재하면 주문 정보가 반환되며, 주문 정보에는 주문자 정보, 주문 항목 수, 총 가격이 포함된다.
     */
    @DisplayName("주문 상세 조회를 할 때, ")
    @Nested
    inner class Get {
        @DisplayName("사용자 정보가 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun failsToGetOrder_whenUserDoesNotExist() {
            // arrange
            val nonExistentUsername = "nonExistentUser"
            val orderCriteria = OrderCriteria.Get(nonExistentUsername, 1L)

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.getOrder(orderCriteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("사용자를 찾을 수 없습니다. username: $nonExistentUsername") },
            )
        }

        @DisplayName("주문이 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun failsToGetOrder_whenOrderDoesNotExist() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val nonExistentOrderId = 999L // 존재하지 않는 주문 ID
            val orderCriteria = OrderCriteria.Get(createdUser.username, nonExistentOrderId)

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.getOrder(orderCriteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("주문을 찾을 수 없습니다. orderId: $nonExistentOrderId") },
            )
        }

        @DisplayName("주문이 존재하면 주문 정보가 반환되며, 주문 정보에는 주문자 정보, 주문 항목 수, 총 가격이 포함된다.")
        @Test
        fun returnsOrderDetail_whenOrderExists() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            val order = anOrder()
                .userId(createdUser.id)
                .orderCustomer(
                    anOrderCustomer()
                        .name("홍길동")
                        .email(Email("shyoon991@gmail.com"))
                        .mobile(Mobile("010-1234-5678"))
                        .address(Address("12345", "서울시 강남구 역삼동", "역삼로 123"))
                        .build(),
                )
                .build()
            order.addItems(
                listOf(
                    anOrderItem()
                        .order(order)
                        .productId(createdProduct.id)
                        .amount(createdProduct.price)
                        .build(),
                ),
            )
            orderJpaRepository.save(order)

            // act
            val orderDetail = orderFacade.getOrder(OrderCriteria.Get(createdUser.username, order.id))

            // assert
            assertAll(
                { assertThat(orderDetail.orderId).isEqualTo(order.id) },
                { assertThat(orderDetail.ordererName).isEqualTo("홍길동") },
                { assertThat(orderDetail.ordererEmail).isEqualTo("shyoon991@gmail.com") },
                { assertThat(orderDetail.ordererMobile).isEqualTo("010-1234-5678") },
                { assertThat(orderDetail.ordererZipCode).isEqualTo("12345") },
                { assertThat(orderDetail.ordererAddress).isEqualTo("서울시 강남구 역삼동") },
                { assertThat(orderDetail.ordererAddressDetail).isEqualTo("역삼로 123") },
                { assertThat(orderDetail.orderItemCount).isEqualTo(1) },
                { assertThat(orderDetail.totalPrice).isEqualTo(createdProduct.price) },
            )
        }
    }

    /*
     **🔗 통합 테스트
    - [ ] 동일한 쿠폰으로 여러 기기에서 동시에 주문해도, 쿠폰은 단 한번만 사용되어야 한다.
    - [ ] 동일한 유저가 서로 다른 주문을 동시에 수행해도, 포인트가 정상적으로 차감되어야 한다.
    - [ ] 동일한 상품에 대해 여러 주문이 동시에 요청되어도, 재고가 정상적으로 차감되어야 한다.
     */
    @DisplayName("결제 타입[포인트]으로 주문 결제 통시성 테스트, ")
    @Nested
    inner class Concurrency {
        @DisplayName("동일한 쿠폰으로 여러 기기에서 동시에 주문해도, 쿠폰은 단 한번만 사용되어야 한다.")
        @Test
        fun shouldUseCouponOnlyOnceWhenConcurrentOrdersArePlacedWithSameCoupon() {
            // arrange
            val numberOfThreads = 2
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val latch = CountDownLatch(numberOfThreads)
            val createdUser = userJpaRepository.save(aUser().build())
            pointJpaRepository.save(aPoint().userId(createdUser.id).point(Point(10_000L)).build())
            val createdProduct = productJpaRepository.save(aProduct().price(Price(5_000L)).build())
            stockJpaRepository.save(aStock().productId(createdProduct.id).build())
            val createdCoupon = couponJpaRepository.save(aCoupon().build())
            val createdIssuedCoupon = issuedCouponJpaRepository.save(anIssuedCoupon().couponId(createdCoupon.id).userId(createdUser.id).build())
            val quantity = Quantity(1)
            val criteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        quantity,
                    ),
                ),
                PaymentMethodType.POINT,
                createdIssuedCoupon.id,
            )
            val successCount = AtomicInteger(0)

            // act
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        orderFacade.placeOrder(criteria)
                        successCount.incrementAndGet()
                    } catch (e: Exception) {
                        println("예외 발생: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            await().during(Duration.ofSeconds(2)).untilAsserted {
                val callOrderCreatedEventCount = applicationEvents.stream(OrderCreatedEvent::class.java).count()
                val callPaymentCompletedEventCount = applicationEvents.stream(PaymentCompletedEvent::class.java).count()
                assertAll(
                    { assertThat(callOrderCreatedEventCount).isEqualTo(successCount.get().toLong()) },
                    { assertThat(callPaymentCompletedEventCount).isEqualTo(successCount.get().toLong()) },
                    { assertThat(issuedCouponJpaRepository.findById(createdIssuedCoupon.id).get().status).isEqualTo(IssuedCouponStatusType.USED) },
                )
            }
        }

        @DisplayName("동일한 유저가 서로 다른 주문을 동시에 수행해도, 포인트가 정상적으로 차감되어야 한다.")
        @Test
        fun shouldDeductPointsCorrectlyWhenConcurrentOrdersArePlacedBySameUser() {
            // arrange
            val numberOfThreads = 2
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val latch = CountDownLatch(numberOfThreads)
            val createdUser = userJpaRepository.save(aUser().build())
            pointJpaRepository.save(aPoint().userId(createdUser.id).point(Point(10_000L)).build())
            val createdProduct = productJpaRepository.save(aProduct().price(Price(5_000L)).build())
            stockJpaRepository.save(aStock().productId(createdProduct.id).build())
            val quantity = Quantity(1)
            val criteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        quantity,
                    ),
                ),
                PaymentMethodType.POINT,
            )
            val successCount = AtomicInteger(0)

            // act
            repeat(numberOfThreads) {
                executor.submit {
                    try {
                        orderFacade.placeOrder(criteria)
                        successCount.incrementAndGet()
                    } catch (e: Exception) {
                        println("예외 발생: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            await().during(Duration.ofSeconds(2)).untilAsserted {
                val callOrderCreatedEventCount = applicationEvents.stream(OrderCreatedEvent::class.java).count()
                val callPaymentCompletedEventCount = applicationEvents.stream(PaymentCompletedEvent::class.java).count()
                assertAll(
                    { assertThat(callOrderCreatedEventCount).isEqualTo(successCount.get().toLong()) },
                    { assertThat(callPaymentCompletedEventCount).isEqualTo(successCount.get().toLong()) },
                    { assertThat(pointJpaRepository.findByUserId(createdUser.id)?.point?.value).isZero() },
                )
            }
        }

        @DisplayName("동일한 상품에 대해 여러 주문이 동시에 요청되어도, 재고가 정상적으로 차감되어야 한다.")
        @Test
        fun shouldNotDeductStockMoreThanAvailableWhenConcurrentOrdersArePlacedForSameProduct() {
            // given
            val numberOfThreads = 2
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdProduct = productJpaRepository.save(aProduct().price(Price(5_000L)).build())
            val createdStock = stockJpaRepository.save(aStock().productId(createdProduct.id).quantity(10).build())
            val quantity = Quantity(1)
            val usernames = mutableListOf<String>()
            val successCount = AtomicInteger(0)

            repeat(numberOfThreads) {
                val createdUser =
                    userJpaRepository.save(aUser().username("user$it").email(Email("shyoon$it@gmail.com")).build())
                pointJpaRepository.save(aPoint().userId(createdUser.id).point(Point(10_000)).build())
                usernames.add(createdUser.username)
            }

            // when
            repeat(numberOfThreads) {
                val criteria = OrderCriteria.Create(
                    usernames[it],
                    "홍길동",
                    Email("shyoon991@gmail.com"),
                    Mobile("010-1234-5678"),
                    Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                    listOf(
                        OrderCriteria.Create.OrderItem(
                            createdProduct.id,
                            quantity,
                        ),
                    ),
                    PaymentMethodType.POINT,
                )
                executor.submit {
                    try {
                        orderFacade.placeOrder(criteria)
                        successCount.incrementAndGet()
                    } catch (e: Exception) {
                        println("실패: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // then
            await().during(Duration.ofSeconds(2)).untilAsserted {
                val remainingStock = stockJpaRepository.findByProductId(createdProduct.id)?.quantity
                println("성공한 주문 수: $successCount")
                println("남은 재고: $remainingStock")

                assertAll(
                    { assertThat(remainingStock).isEqualTo(createdStock.quantity - successCount.get()) },
                )
            }
        }
    }

    /*
     **🔗 통합 테스트
    - [ ] 결제 타입[카드]으로 주문을 생성할 때, 결제 요청이 성공하면 결제 상태는 PENDING 상태로 저장된다.
    - [ ] 결제 타입[카드]으로 주문을 생성할 때, 결제 요청이 실패하면 결제 상태는 FAILED 상태로 저장된다.
     */
    @DisplayName("결제 타입[카드]으로 주문을 생성할 때, ")
    @Nested
    inner class CreateByCardType {
        @DisplayName("결제 요청이 성공하면 결제 상태는 PENDING 상태로 저장된다.")
        @Test
        fun succeedsToCreateOrder_whenPaymentIsSuccessful() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            stockJpaRepository.save(aStock().build())
            val quantity = Quantity(2)
            val orderCriteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        quantity,
                    ),
                ),
                PaymentMethodType.CARD,
                cardType = PaymentCardType.SAMSUNG,
                cardNo = "1234-5678-9012-3456",
            )

            whenever(paymentGateway.requestPayment(any(), any()))
                .thenReturn(PaymentGatewayResult.Requested(transactionKey = "pg_tx_123", status = PaymentStatusType.PENDING))

            // act
            val orderId = orderFacade.placeOrder(orderCriteria)

            // assert
            val findOrder = orderJpaRepository.findWithItemsById(orderId)
            val findPayment = paymentJpaRepository.findByOrderId(orderId)
            findOrder?.let { order ->
                assertAll(
                    { assertThat(order.userId).isEqualTo(createdUser.id) },
                    { assertThat(order.orderStatus).isEqualTo(OrderStatusType.PENDING) },
                    { assertThat(order.orderItems.size()).isEqualTo(2) },
                    { assertThat(order.orderItems.amount()).isEqualTo(Price(createdProduct.price.value * quantity.value)) },
                )
            }
            verify(paymentGateway, times(1)).requestPayment(any(), any())
            findPayment?.let { payment ->
                assertAll(
                    { assertThat(payment.orderId).isEqualTo(orderId) },
                    { assertThat(payment.status).isEqualTo(PaymentStatusType.PENDING) },
                    { assertThat(payment.transactionKey).isEqualTo("pg_tx_123") },
                )
            }
        }

        @DisplayName("결제 요청이 실패하면 결제 상태는 FAILED 상태로 저장된다.")
        @Test
        fun failsToCreateOrder_whenPaymentFails() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().build())
            stockJpaRepository.save(aStock().build())
            val quantity = Quantity(2)
            val orderCriteria = OrderCriteria.Create(
                createdUser.username,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItem(
                        createdProduct.id,
                        quantity,
                    ),
                ),
                PaymentMethodType.CARD,
                cardType = PaymentCardType.SAMSUNG,
                cardNo = "1234-5678-9012-3456",
            )

            whenever(paymentGateway.requestPayment(any(), any()))
                .thenReturn(PaymentGatewayResult.Requested(status = PaymentStatusType.FAILED))

            // act
            val orderId = orderFacade.placeOrder(orderCriteria)

            // assert
            val findOrder = orderJpaRepository.findWithItemsById(orderId)
            val findPayment = paymentJpaRepository.findByOrderId(orderId)
            findOrder?.let { order ->
                assertAll(
                    { assertThat(order.userId).isEqualTo(createdUser.id) },
                    { assertThat(order.orderStatus).isEqualTo(OrderStatusType.PENDING) },
                    { assertThat(order.orderItems.size()).isEqualTo(2) },
                    { assertThat(order.orderItems.amount()).isEqualTo(Price(createdProduct.price.value * quantity.value)) },
                )
            }
            verify(paymentGateway, times(1)).requestPayment(any(), any())
            findPayment?.let { payment ->
                assertAll(
                    { assertThat(payment.orderId).isEqualTo(orderId) },
                    { assertThat(payment.status).isEqualTo(PaymentStatusType.FAILED) },
                    { assertThat(payment.transactionKey).isNull() },
                )
            }
        }
    }
}
