package com.loopers.domain.order

import com.loopers.domain.order.OrderEntityFixture.Companion.anOrder
import com.loopers.domain.order.OrderItemEntityFixture.Companion.anOrderItem
import com.loopers.domain.order.vo.OrderCustomerFixture.Companion.anOrderCustomer
import com.loopers.domain.order.vo.OrderItems
import com.loopers.domain.order.vo.Quantity
import com.loopers.domain.vo.Price
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class OrderEntityTest {

    /*
    * 🧱 단위 테스트
    - [ ] 주문 아이템 목록에서 주문의 총 금액을 계산한다.
    - [ ] 주문 생성 시 상태는 PENDING이다.
    - [ ] 주문을 완료 처리하면 상태는 COMPLETED가 된다
    - [ ] 주문을 취소하면 상태는 CANCELLED가 된다
     */
    @DisplayName("주문 엔티티를 생성 할 때, ")
    @Nested
    inner class Create {
        @DisplayName("주문 아이템 목록에서 주문의 총 금액을 계산한다.")
        @Test
        fun calculatesTotalPriceFromProductList() {
            // arrange

            // act
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .orderItems(
                    OrderItems(
                        mutableListOf(
                            anOrderItem()
                                .quantity(Quantity(2))
                                .price(Price(2000))
                                .totalPrice(Price(2000))
                                .build(),
                            anOrderItem()
                                .quantity(Quantity(1))
                                .price(Price(3000))
                                .totalPrice(Price(3000))
                                .build(),
                        ),
                    ),
                )
                .build()

            // assert
            assertAll(
                { assertThat(order.totalPrice.value).isEqualTo(5000) },
                { assertThat(order.amount.value).isEqualTo(5000) },
            )
        }

        @DisplayName("주문 생성 시 상태는 PENDING이다.")
        @Test
        // 영어로
        fun createsOrderWithPendingStatus() {
            // arrange

            // act
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .orderItems(
                    OrderItems(
                        mutableListOf(
                            anOrderItem()
                                .quantity(Quantity(2))
                                .price(Price(2000))
                                .totalPrice(Price(2000))
                                .build(),
                            anOrderItem()
                                .quantity(Quantity(1))
                                .price(Price(3000))
                                .totalPrice(Price(3000))
                                .build(),
                        ),
                    ),
                )
                .build()

            // assert
            assertThat(order.orderStatus).isEqualTo(OrderEntity.OrderStatusType.PENDING)
        }

        @DisplayName("주문을 완료 처리하면 상태는 COMPLETED가 된다")
        @Test
        fun canChangeOrderToCompletedStatus() {
            // arrange

            // act
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .orderItems(
                    OrderItems(
                        mutableListOf(
                            anOrderItem()
                                .quantity(Quantity(2))
                                .price(Price(2000))
                                .totalPrice(Price(2000))
                                .build(),
                            anOrderItem()
                                .quantity(Quantity(1))
                                .price(Price(3000))
                                .totalPrice(Price(3000))
                                .build(),
                        ),
                    ),
                )
                .build()

            // act
            order.complete()

            assertThat(order.orderStatus).isEqualTo(OrderEntity.OrderStatusType.COMPLETED)
        }

        @DisplayName("주문을 취소하면 상태는 CANCELLED가 된다")
        @Test
        fun canChangeOrderToCancelledStatus() {
            // arrange

            // act
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .orderItems(
                    OrderItems(
                        mutableListOf(
                            anOrderItem()
                                .quantity(Quantity(2))
                                .price(Price(2000))
                                .totalPrice(Price(2000))
                                .build(),
                            anOrderItem()
                                .quantity(Quantity(1))
                                .price(Price(3000))
                                .totalPrice(Price(3000))
                                .build(),
                        ),
                    ),
                )
                .build()

            // act
            order.cancel()

            // assert
            assertThat(order.orderStatus).isEqualTo(OrderEntity.OrderStatusType.CANCELED)
        }
    }
}
