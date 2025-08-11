package com.loopers.application.order

import com.loopers.domain.coupon.fixture.CouponEntityFixture.Companion.aCoupon
import com.loopers.domain.coupon.fixture.IssuedCouponEntityFixture.Companion.anIssuedCoupon
import com.loopers.domain.order.OrderRepository
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
import com.loopers.domain.vo.Quantity
import com.loopers.infrastructure.coupon.CouponJpaRepository
import com.loopers.infrastructure.coupon.IssuedCouponJpaRepository
import com.loopers.support.StockServiceMockConfig
import com.loopers.support.enums.order.OrderStatusType
import com.loopers.support.enums.payment.PaymentMethodType
import com.loopers.support.error.ErrorType
import com.loopers.support.error.payment.StockDeductionFailedException
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
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
    private val couponJpaRepository: CouponJpaRepository,
    private val issuedCouponJpaRepository: IssuedCouponJpaRepository,
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
    - [ ] 결제 성공 후 재고 감소에 실패하면 포인트, 쿠폰은 원복하고 결제/주문은 실패한다.
     */
    @DisplayName("결제 성공 후 재고 감소에 실패할 때, 포인트는")
    @Nested
    inner class StockReductionFailure {
        @DisplayName("결제 성공 후 재고 감소에 실패하면 포인트, 쿠폰은 원복하고 결제/주문은 실패한다.")
        @Test
        fun failsToPayWithPoints_whenStockReductionFails() {
            // arrange
            val createdUser = userRepository.save(aUser().build())
            val createdPoint = pointRepository.save(aPoint().userId(createdUser.id).point(Point(10_000)).build())
            val createdProduct = productRepository.save(aProduct().price(Price(1000)).build())
            val createdStock = stockRepository.save(aStock().productId(createdProduct.id).build())
            val createdCoupon = couponJpaRepository.save(aCoupon().build())
            val createdIssuedCoupon = issuedCouponJpaRepository.save(anIssuedCoupon().userId(createdUser.id).couponId(createdCoupon.id).build())
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
                createdIssuedCoupon.id,
            )

            whenever(stockService.getStocksByProductIds(listOf(createdProduct.id))).thenReturn(
                listOf(aStock().productId(createdProduct.id).quantity(5).build()),
            )
            whenever(stockService.deductStockQuantities(any<List<StockCommand.Deduct>>()))
                .thenThrow(StockDeductionFailedException(ErrorType.CONFLICT, "재고가 부족합니다."))

            // act
            val orderId = orderFacade.placeOrder(criteria)

            // assert
            assertAll(
                { assertThat(orderRepository.findWithItemsById(orderId)?.orderStatus).isEqualTo(OrderStatusType.CANCELED) },
                { assertThat(pointRepository.findByUserId(createdUser.id)?.point).isEqualTo(createdPoint.point) },
                { assertThat(stockRepository.findByProductId(createdProduct.id)?.quantity).isEqualTo(createdStock.quantity) },
                { assertThat(issuedCouponJpaRepository.findById(createdIssuedCoupon.id).get().isUsed()).isFalse() },
            )
        }
    }
}
