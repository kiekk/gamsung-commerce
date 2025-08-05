package com.loopers.application.order

import com.loopers.domain.order.OrderEntityFixture.Companion.anOrder
import com.loopers.domain.order.OrderItemEntityFixture.Companion.anOrderItem
import com.loopers.domain.order.vo.OrderCustomerFixture.Companion.anOrderCustomer
import com.loopers.domain.order.vo.Quantity
import com.loopers.domain.point.PointEntityFixture.Companion.aPoint
import com.loopers.domain.point.vo.Point
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.stock.fixture.StockEntityFixture.Companion.aStock
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.domain.vo.Address
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Mobile
import com.loopers.domain.vo.Price
import com.loopers.infrastructure.order.OrderJpaRepository
import com.loopers.infrastructure.payment.PaymentJpaRepository
import com.loopers.infrastructure.point.PointJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.stock.StockJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.enums.order.OrderStatusType
import com.loopers.support.enums.payment.PaymentMethodType
import com.loopers.support.enums.payment.PaymentStatusType
import com.loopers.support.enums.product.ProductStatusType
import com.loopers.support.error.CoreException
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class OrderFacadeIntegrationTest @Autowired constructor(
    private val orderFacade: OrderFacade,
    private val userJpaRepository: UserJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
    private val stockJpaRepository: StockJpaRepository,
    private val paymentJpaRepository: PaymentJpaRepository,
    private val pointJpaRepository: PointJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
    private val orderJpaRepository: OrderJpaRepository,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
     **🔗 통합 테스트
    - [ ] 존재하지 않는 사용자가 주문을 요청할 경우 예외가 발생한다.
    - [ ] 주문 항목의 productId에 해당하는 상품이 존재하지 않으면 예외가 발생한다.
    - [ ] 주문 항목의 productId에 해당하는 상품이 주문 가능한 상태가 아니면 예외가 발생한다.
    - [ ] 주문 항목의 수량이 상품의 재고를 초과하면 예외가 발생한다.
    - [ ] 사용자, 상품 정보, 상품 상태, 상품 재고가 모두 유효한 경우 주문이 성공적으로 생성된다.
     */
    @DisplayName("주문을 생성할 때, ")
    @Nested
    open inner class Create {

        @DisplayName("존재하지 않는 사용자가 주문을 요청할 경우 예외가 발생한다.")
        @Test
        fun failsToCreateOrder_whenUserDoesNotExist() {
            // arrange
            val nonExistentUserId = 999L // 존재하지 않는 사용자 ID
            val createdProduct = productJpaRepository.save(aProduct().build())
            val orderCriteria = OrderCriteria.Create(
                nonExistentUserId,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItemCriteria(
                        createdProduct.id,
                        createdProduct.name,
                        Quantity(2),
                        createdProduct.price,
                        createdProduct.price,
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
                { assertThat(exception.message).isEqualTo("사용자를 찾을 수 없습니다. userId: $nonExistentUserId") },
            )
        }

        @DisplayName("주문 항목의 productId에 해당하는 상품이 존재하지 않으면 예외가 발생한다.")
        @Test
        fun failsToCreateOrder_whenProductDoesNotExist() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val nonExistentProductId = 999L
            val orderCriteria = OrderCriteria.Create(
                createdUser.id,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItemCriteria(
                        nonExistentProductId,
                        "존재하지 않는 상품",
                        Quantity(2),
                        Price(20_000),
                        Price(20_000),
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
            val createdProduct = productJpaRepository.save(aProduct().status(ProductStatusType.INACTIVE).build())
            val orderCriteria = OrderCriteria.Create(
                createdUser.id,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItemCriteria(
                        createdProduct.id,
                        createdProduct.name,
                        Quantity(2),
                        createdProduct.price,
                        createdProduct.price,
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
                createdUser.id,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItemCriteria(
                        createdProduct.id,
                        createdProduct.name,
                        quantity,
                        createdProduct.price,
                        createdProduct.price,
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
                createdUser.id,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItemCriteria(
                        createdProduct.id,
                        createdProduct.name,
                        quantity,
                        createdProduct.price,
                        createdProduct.price,
                    ),
                ),
                PaymentMethodType.POINT,
            )

            // act
            val orderId = orderFacade.placeOrder(criteria)

            // assert
            val findOrder = orderJpaRepository.findWithItemsById(orderId)
            findOrder?.let { order ->
                assertAll(
                    { assertThat(order.userId).isEqualTo(createdUser.id) },
                    { assertThat(order.orderStatus).isEqualTo(OrderStatusType.COMPLETED) },
                    { assertThat(order.orderItems.size()).isEqualTo(2) },
                    { assertThat(order.orderItems.amount()).isEqualTo(Price(createdProduct.price.value * quantity.value)) },
                    { assertThat(order.orderItems.totalPrice()).isEqualTo(Price(createdProduct.price.value * quantity.value)) },
                )
            }
            val findPayment = paymentJpaRepository.findWithItemsByOrderId(orderId)
            findPayment?.let { payment ->
                assertAll(
                    { assertThat(payment.status).isEqualTo(PaymentStatusType.COMPLETED) },
                    { assertThat(payment.paymentItems.isAllCompleted()).isTrue() },
                    { assertThat(payment.totalAmount).isEqualTo(findOrder?.amount) },
                )
            }
            val findPoint = pointJpaRepository.findByUserId(createdUser.id)
            assertThat(findPoint?.point).isEqualTo(Point(createdPoint.point.value - (createdProduct.price.value * quantity.value)))
        }

        @DisplayName("포인트 정보가 없을 경우 예외가 발생하고 주문 정보는 생성되지 않는다.")
        @Test
        fun failsToPayWithPoints_whenPointInfoIsMissing() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdProduct = productJpaRepository.save(aProduct().price(Price(1000)).build())
            stockJpaRepository.save(aStock().build())
            val quantity = Quantity(2)
            val criteria = OrderCriteria.Create(
                createdUser.id,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItemCriteria(
                        createdProduct.id,
                        createdProduct.name,
                        quantity,
                        createdProduct.price,
                        createdProduct.price,
                    ),
                ),
                PaymentMethodType.POINT,
            )

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.placeOrder(criteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("사용자 포인트를 찾을 수 없습니다.") },
                { assertThat(orderJpaRepository.findWithItemsById(criteria.userId)).isNull() },
            )
        }

        @DisplayName("포인트 부족 시 예외가 발생하고 주문 정보는 생성되지 않는다.")
        @Test
        fun failsToPayWithPoints_whenPaymentFails() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdPoint = pointJpaRepository.save(aPoint().userId(createdUser.id).point(Point(1000)).build())
            val createdProduct = productJpaRepository.save(aProduct().price(Price(1000)).build())
            stockJpaRepository.save(aStock().build())
            val quantity = Quantity(2)
            val criteria = OrderCriteria.Create(
                createdUser.id,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItemCriteria(
                        createdProduct.id,
                        createdProduct.name,
                        quantity,
                        createdProduct.price,
                        createdProduct.price,
                    ),
                ),
                PaymentMethodType.POINT,
            )

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.placeOrder(criteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("포인트로 결제할 수 없습니다. 사용 가능한 포인트: ${createdPoint.point}") },
                { assertThat(orderJpaRepository.findWithItemsById(criteria.userId)).isNull() },
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
            val nonExistentUserId = 999L // 존재하지 않는 사용자 ID
            val orderCriteria = OrderCriteria.Get(nonExistentUserId, 1L)

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.getOrderById(orderCriteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("사용자를 찾을 수 없습니다. userId: $nonExistentUserId") },
            )
        }

        @DisplayName("주문이 존재하지 않으면 404 Not Found 예외가 발생한다.")
        @Test
        fun failsToGetOrder_whenOrderDoesNotExist() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val nonExistentOrderId = 999L // 존재하지 않는 주문 ID
            val orderCriteria = OrderCriteria.Get(createdUser.id, nonExistentOrderId)

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.getOrderById(orderCriteria)
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
                        .totalPrice(createdProduct.price)
                        .build(),
                ),
            )
            orderJpaRepository.save(order)

            // act
            val orderDetail = orderFacade.getOrderById(OrderCriteria.Get(createdUser.id, order.id))

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
    - [ ] 동일한 유저가 여러 기기에서 동시에 주문해도 포인트가 중복 차감되지 않아야 한다.
    - [ ] 동일한 상품에 대해 여러 주문이 동시에 요청되어도, 재고가 정상적으로 차감되어야 한다.
     */
    @DisplayName("주문 결제 통시성 테스트, ")
    @Nested
    inner class Concurrency {
        @DisplayName("동일한 유저가 여러 기기에서 동시에 주문해도 포인트가 중복 차감되지 않아야 한다")
        @Test
        fun shouldNotDeductPointsMultipleTimesWhenSameUserOrdersConcurrently() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val createdPoint = pointJpaRepository.save(aPoint().userId(createdUser.id).point(Point(10_000L)).build())
            val createdProduct = productJpaRepository.save(aProduct().price(Price(5_000L)).build())
            stockJpaRepository.save(aStock().productId(createdProduct.id).build())
            val quantity = Quantity(1)
            val criteria = OrderCriteria.Create(
                createdUser.id,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCriteria.Create.OrderItemCriteria(
                        createdProduct.id,
                        createdProduct.name,
                        quantity,
                        createdProduct.price,
                        createdProduct.price,
                    ),
                ),
                PaymentMethodType.POINT,
            )

            val threadCount = 10
            val executor = Executors.newFixedThreadPool(threadCount)
            val latch = CountDownLatch(threadCount)

            // act
            var orderId: Long? = null
            repeat(threadCount) {
                executor.submit {
                    try {
                        orderId = orderFacade.placeOrder(criteria)
                    } catch (e: Exception) {
                        println("예외 발생: ${e.message}")
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // assert
            val findOrder = orderJpaRepository.findWithItemsById(orderId!!)
            assertThat(findOrder).isNotNull

            val findPoint = pointJpaRepository.findByUserId(createdUser.id)
            assertThat(findPoint?.point).isEqualTo(Point(createdPoint.point.value - (createdProduct.price.value * quantity.value)))
        }

        @DisplayName("동일한 상품에 대해 여러 주문이 동시에 요청되어도, 재고가 정상적으로 차감되어야 한다.")
        @Test
        fun shouldNotDeductStockMoreThanAvailableWhenConcurrentOrdersArePlacedForSameProduct() {
            // given
            val numberOfThreads = 20
            val latch = CountDownLatch(numberOfThreads)
            val executor = Executors.newFixedThreadPool(numberOfThreads)
            val createdProduct = productJpaRepository.save(aProduct().price(Price(5_000L)).build())
            val createdStock = stockJpaRepository.save(aStock().productId(createdProduct.id).quantity(10).build())
            val quantity = Quantity(1)
            val userIds = mutableListOf<Long>()
            var failureCount = 0
            var successCount = 0

            repeat(numberOfThreads) {
                val createdUser =
                    userJpaRepository.save(aUser().username("user$it").email(Email("shyoon$it@gmail.com")).build())
                pointJpaRepository.save(aPoint().userId(createdUser.id).point(Point(10_000)).build())
                userIds.add(createdUser.id)
            }

            // when
            repeat(numberOfThreads) {
                val criteria = OrderCriteria.Create(
                    userIds[it],
                    "홍길동",
                    Email("shyoon991@gmail.com"),
                    Mobile("010-1234-5678"),
                    Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                    listOf(
                        OrderCriteria.Create.OrderItemCriteria(
                            createdProduct.id,
                            createdProduct.name,
                            quantity,
                            createdProduct.price,
                            createdProduct.price,
                        ),
                    ),
                    PaymentMethodType.POINT,
                )
                executor.submit {
                    try {
                        orderFacade.placeOrder(criteria)
                        successCount++
                    } catch (e: Exception) {
                        println("실패: ${e.message}")
                        failureCount++
                    } finally {
                        latch.countDown()
                    }
                }
            }

            latch.await()

            // then
            val remainingStock = stockJpaRepository.findByProductId(createdProduct.id)?.quantity
            println("낙관적 락 충돌로 인한 실패 수: $failureCount")
            println("성공한 주문 수: $successCount")
            println("남은 재고: $remainingStock")
            assertThat(remainingStock).isEqualTo(createdStock.quantity - successCount)
        }
    }
}

