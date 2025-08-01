package com.loopers.application.order

import com.loopers.domain.order.OrderEntity
import com.loopers.domain.order.OrderRepository
import com.loopers.domain.order.vo.Quantity
import com.loopers.domain.payment.PaymentEntity
import com.loopers.domain.payment.PaymentRepository
import com.loopers.domain.point.PointEntityFixture.Companion.aPoint
import com.loopers.domain.point.PointRepository
import com.loopers.domain.point.vo.Point
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.stock.StockRepository
import com.loopers.domain.stock.fixture.StockEntityFixture.Companion.aStock
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.domain.user.UserRepository
import com.loopers.domain.vo.Address
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Mobile
import com.loopers.domain.vo.Price
import com.loopers.support.enums.product.ProductStatusType
import com.loopers.support.error.CoreException
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OrderFacadeIntegrationTest @Autowired constructor(
    private val orderFacade: OrderFacade,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository,
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val pointRepository: PointRepository,
    private val databaseCleanUp: DatabaseCleanUp,
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
            val createdProduct = productRepository.save(aProduct().build())
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
                PaymentEntity.PaymentMethodType.POINT,
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
            val createdUser = userRepository.save(aUser().build())
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
                PaymentEntity.PaymentMethodType.POINT,
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
            val createdUser = userRepository.save(aUser().build())
            val createdProduct = productRepository.save(aProduct().status(ProductStatusType.INACTIVE).build())
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
                PaymentEntity.PaymentMethodType.POINT,
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
            val createdUser = userRepository.save(aUser().build())
            val createdProduct = productRepository.save(aProduct().build())
            val createdStock = stockRepository.save(aStock().quantity(0).build())
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
                PaymentEntity.PaymentMethodType.POINT,
            )

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.placeOrder(orderCriteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("재고가 부족한 상품입니다. productId: ${createdProduct.id}, 요청 수량: $quantity, 재고: ${createdStock.quantity}") },
            )
        }
    }

    /*
     **🔗 통합 테스트
    - [ ] 포인트로 결제에 성공하면 재고가 감소하며 결제 성공, 주문 완료 처리 된다.
    - [ ] 포인트 정보가 없을 경우 예외가 발생하고 주문 정보는 생성되지 않는다.
    - [ ] 포인트 부족 시 결제는 실패하고 주문도 실패한다.
     */
    @DisplayName("주문을 결제할 때, ")
    @Nested
    inner class Payment {
        @DisplayName("포인트로 결제에 성공하면 재고가 감소하며 결제 성공, 주문 완료 처리 된다.")
        @Test
        fun succeedsToPayWithPoints_whenPaymentIsSuccessful() {
            // arrange
            val createdUser = userRepository.save(aUser().build())
            val createdPoint = pointRepository.save(aPoint().userId(createdUser.userId).point(Point(20_000)).build())
            val createdProduct = productRepository.save(aProduct().price(Price(1000)).build())
            stockRepository.save(aStock().build())
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
                PaymentEntity.PaymentMethodType.POINT,
            )

            // act
            val orderId = orderFacade.placeOrder(criteria)

            // assert
            val findOrder = orderRepository.findWithItemsById(orderId)
            findOrder?.let { order ->
                assertAll(
                    { assertThat(order.userId).isEqualTo(createdUser.id) },
                    { assertThat(order.orderStatus).isEqualTo(OrderEntity.OrderStatusType.COMPLETED) },
                    { assertThat(order.orderItems.size()).isEqualTo(2) },
                    { assertThat(order.orderItems.amount()).isEqualTo(Price(createdProduct.price.value * quantity.value)) },
                    { assertThat(order.orderItems.totalPrice()).isEqualTo(Price(createdProduct.price.value * quantity.value)) },
                )
            }
            val findPayment = paymentRepository.findWithItemsByOrderId(orderId)
            findPayment?.let { payment ->
                assertAll(
                    { assertThat(payment.status).isEqualTo(PaymentEntity.PaymentStatusType.COMPLETED) },
                    { assertThat(payment.paymentItems.isAllCompleted()).isTrue() },
                    { assertThat(payment.totalAmount).isEqualTo(findOrder?.amount) },
                )
            }
            val findPoint = pointRepository.findByUserId(createdUser.userId)
            assertThat(findPoint?.point).isEqualTo(Point(createdPoint.point.value - (createdProduct.price.value * quantity.value)))
        }

        @DisplayName("포인트 정보가 없을 경우 예외가 발생하고 주문 정보는 생성되지 않는다.")
        @Test
        fun failsToPayWithPoints_whenPointInfoIsMissing() {
            // arrange
            val createdUser = userRepository.save(aUser().build())
            val createdProduct = productRepository.save(aProduct().price(Price(1000)).build())
            stockRepository.save(aStock().build())
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
                PaymentEntity.PaymentMethodType.POINT,
            )

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.placeOrder(criteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("사용자 포인트를 찾을 수 없습니다.") },
                { assertThat(orderRepository.findWithItemsById(criteria.userId)).isNull() },
            )
        }

        @DisplayName("포인트 부족 시 결제는 실패하고 주문도 실패한다.")
        @Test
        fun failsToPayWithPoints_whenPaymentFails() {
            // arrange
            val createdUser = userRepository.save(aUser().build())
            val createdPoint = pointRepository.save(aPoint().userId(createdUser.userId).point(Point(1000)).build())
            val createdProduct = productRepository.save(aProduct().price(Price(1000)).build())
            stockRepository.save(aStock().build())
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
                PaymentEntity.PaymentMethodType.POINT,
            )

            // act
            val exception = assertThrows<CoreException> {
                orderFacade.placeOrder(criteria)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).isEqualTo("포인트로 결제할 수 없습니다. 사용 가능한 포인트: ${createdPoint.point}") },
                { assertThat(orderRepository.findWithItemsById(criteria.userId)).isNull() },
            )
        }
    }
}
