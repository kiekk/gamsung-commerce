package com.loopers.application.order

import com.loopers.domain.order.OrderRepository
import com.loopers.domain.order.vo.Quantity
import com.loopers.domain.payment.PaymentRepository
import com.loopers.domain.point.PointEntityFixture.Companion.aPoint
import com.loopers.domain.point.PointRepository
import com.loopers.domain.point.vo.Point
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockRepository
import com.loopers.domain.stock.StockService
import com.loopers.domain.stock.fixture.StockEntityFixture.Companion.aStock
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.domain.user.UserRepository
import com.loopers.domain.vo.Address
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Mobile
import com.loopers.domain.vo.Price
import com.loopers.support.StockServiceMockConfig
import com.loopers.support.enums.payment.PaymentMethodType
import com.loopers.support.error.ErrorType
import com.loopers.support.error.payment.PaymentException
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(StockServiceMockConfig::class)
class OrderFacadePaymentFailureTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val stockRepository: StockRepository,
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val pointRepository: PointRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @Autowired
    private lateinit var orderFacade: OrderFacade

    @Autowired
    private lateinit var stockService: StockService

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
     **🔗 통합 테스트
    - [ ] 결제 성공 후 재고 감소에 실패하면 포인트는 원복하고 결제/주문은 실패한다.
    - [ ] 결제 성공 후 재고 감소에 실패하면 주문 생성, 결제 처리, 포인트 차감 모두 롤백처리되어야 한다.
     */
    @DisplayName("결제 성공 후 재고 감소에 실패할 때, 포인트는")
    @Nested
    inner class StockReductionFailure {
        /*
        TODO: 3주차에는 재고 차감 예외 발생 시 포인트 원복 및 결제/주문 실패 처리로 구현하였으나,
        4주차에는 재고 차감 예외 발생 시 모두 롤백처리로 구현해야 하므로 해당 테스트 케이스를 주석 처리.
        추후 다시 원복 시나리오로 구현할 경우 로직 수정 및 주석 해제 필요
         */
        @DisplayName("결제 성공 후 재고 감소에 실패하면 포인트는 원복하고 결제/주문은 실패한다.")
//        @Test
        fun failsToPayWithPoints_whenStockReductionFails() {
            // arrange
            val createdUser = userRepository.save(aUser().build())
            val createdPoint = pointRepository.save(aPoint().userId(createdUser.id).point(Point(10_000)).build())
            val createdProduct = productRepository.save(aProduct().price(Price(1000)).build())
            val createdStock = stockRepository.save(aStock().build())
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

            whenever(stockService.getStocksByProductIds(listOf(createdProduct.id))).thenReturn(
                listOf(aStock().productId(createdProduct.id).quantity(5).build()),
            )
            whenever(stockService.deductStockQuantities(any<List<StockCommand.Decrease>>()))
                .thenThrow(PaymentException(ErrorType.CONFLICT, "재고가 부족합니다."))

            // act
            orderFacade.placeOrder(criteria)

            // assert
            assertAll(
                { assertThat(pointRepository.findByUserId(createdUser.id)?.point).isEqualTo(createdPoint.point) },
                { assertThat(paymentRepository.findWithItemsByOrderId(criteria.userId)).isNull() },
                { assertThat(stockRepository.findByProductId(createdProduct.id)?.quantity).isEqualTo(createdStock.quantity) },
                { assertThat(orderRepository.findWithItemsById(criteria.userId)).isNull() },
            )
        }

        @DisplayName("결제 성공 후 재고 감소에 실패하면 주문 생성, 결제 처리, 포인트 차감 모두 롤백처리되어야 한다.")
        @Test
        fun failsToPlaceOrder_whenStockReductionFails() {
            // arrange
            val createdUser = userRepository.save(aUser().build())
            val createdPoint = pointRepository.save(aPoint().userId(createdUser.id).point(Point(10_000)).build())
            val createdProduct = productRepository.save(aProduct().price(Price(1000)).build())
            val createdStock = stockRepository.save(aStock().build())
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

            whenever(stockService.getStocksByProductIds(listOf(createdProduct.id))).thenReturn(
                listOf(aStock().productId(createdProduct.id).quantity(5).build()),
            )
            whenever(stockService.deductStockQuantities(any<List<StockCommand.Decrease>>()))
                .thenThrow(PaymentException(ErrorType.CONFLICT, "재고가 부족합니다."))

            // act
            val exception = assertThrows(PaymentException::class.java) {
                orderFacade.placeOrder(criteria)
            }

            // assert
            assertAll(
                { assertThat(exception.message).contains("재고가 부족합니다.") },
                { assertThat(pointRepository.findByUserId(createdUser.id)?.point).isEqualTo(createdPoint.point) },
                { assertThat(paymentRepository.findWithItemsByOrderId(criteria.userId)).isNull() },
                { assertThat(stockRepository.findByProductId(createdProduct.id)?.quantity).isEqualTo(createdStock.quantity) },
                { assertThat(orderRepository.findWithItemsById(criteria.userId)).isNull() },
            )
        }
    }
}
