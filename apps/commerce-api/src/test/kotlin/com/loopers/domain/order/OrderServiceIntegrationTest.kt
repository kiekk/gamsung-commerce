package com.loopers.domain.order

import com.loopers.domain.order.vo.Quantity
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.vo.Address
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Mobile
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class OrderServiceIntegrationTest @Autowired constructor(
    private val orderService: OrderService,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
    **🔗 통합 테스트
    - [ ] 주문이 생성되면 주문에서 주문의 총 금액을 계산한다.
     */
    @DisplayName("주문을 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("주문이 생성되면 주문에서 주문의 총 금액을 계산한다.")
        @Test
        fun calculatesTotalAmount_whenOrderIsCreated() {
            // arrange
            val createdProduct = productRepository.save(aProduct().build())
            val orderCommand = OrderCommand.Create(
                1L,
                "홍길동",
                Email("shyoon991@gmail.com"),
                Mobile("010-1234-5678"),
                Address("12345", "서울시 강남구 역삼동", "역삼로 123"),
                listOf(
                    OrderCommand.Create.OrderItemCommand(
                        createdProduct.id,
                        createdProduct.name,
                        Quantity(2),
                        createdProduct.price,
                        createdProduct.price,
                    ),
                ),
            )
            // act
            val createdOrder = orderService.createOrder(orderCommand)

            // assert
            val findOrder = orderRepository.findWithItemsById(createdOrder.id)
            assertAll(
                { assertThat(createdOrder.id).isEqualTo(findOrder?.id) },
                { assertThat(createdOrder.orderStatus).isEqualTo(findOrder?.orderStatus) },
                { assertThat(createdOrder.totalPrice).isEqualTo(findOrder?.totalPrice) },
                { assertThat(createdOrder.amount).isEqualTo(findOrder?.amount) },
            )
        }
    }

}
