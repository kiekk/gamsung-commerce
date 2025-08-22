package com.loopers.domain.order

import com.loopers.domain.order.fixture.OrderEntityFixture.Companion.anOrder
import com.loopers.domain.order.fixture.OrderItemEntityFixture.Companion.anOrderItem
import com.loopers.domain.order.vo.OrderCustomerFixture.Companion.anOrderCustomer
import com.loopers.domain.vo.Price
import com.loopers.support.enums.order.OrderStatusType
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
    - [ ] 주문을 취소하면 상태는 CANCELED가 된다
    - [ ] 주문을 실패 처리하면 상태는 FAILED가 된다
    - [ ] 주문 상태가 COMPLETED인 경우 isCompleted() 메서드는 true를 반환한다.
    - [ ] 주문 상태가 COMPLETED가 아닌 경우 isCompleted() 메서드는 false를 반환한다.
    - [ ] 주문 금액과 비교하려는 금액이 같으면 isNotEqualAmount() 메서드는 false를 반환한다.
    - [ ] 주문 금액과 비교하려는 금액이 다르면 isNotEqualAmount() 메서드는 true를 반환한다.
     */
    @DisplayName("주문 엔티티를 생성 할 때, ")
    @Nested
    inner class Create {
        @DisplayName("주문 아이템 목록에서 주문의 총 금액을 계산한다.")
        @Test
        fun calculatesTotalPriceFromProductList() {
            // arrange
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .build()

            order.addItems(
                listOf(
                    anOrderItem()
                        .amount(Price(2000))
                        .build(),
                    anOrderItem()
                        .amount(Price(3000))
                        .build(),
                ),
            )

            // assert
            assertAll(
                { assertThat(order.totalPrice.value).isEqualTo(5000) },
                { assertThat(order.amount.value).isEqualTo(5000) },
            )
        }

        // 영어로
        @DisplayName("주문 생성 시 상태는 PENDING이다.")
        @Test
        fun createsOrderWithPendingStatus() {
            // arrange

            // act
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .build()
            order.addItems(
                listOf(
                    anOrderItem()
                        .amount(Price(2000))
                        .build(),
                    anOrderItem()
                        .amount(Price(3000))
                        .build(),
                ),
            )

            // assert
            assertThat(order.orderStatus).isEqualTo(OrderStatusType.PENDING)
        }

        @DisplayName("주문을 완료 처리하면 상태는 COMPLETED가 된다")
        @Test
        fun canChangeOrderToCompletedStatus() {
            // arrange
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .build()

            order.addItems(
                listOf(
                    anOrderItem()
                        .amount(Price(2000))
                        .build(),
                    anOrderItem()
                        .amount(Price(3000))
                        .build(),
                ),
            )

            // act
            order.complete()

            assertThat(order.orderStatus).isEqualTo(OrderStatusType.COMPLETED)
        }

        @DisplayName("주문을 취소하면 상태는 CANCELED가 된다")
        @Test
        fun canChangeOrderToCanceledStatus() {
            // arrange
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .build()

            order.addItems(
                listOf(
                    anOrderItem()
                        .amount(Price(2000))
                        .build(),
                    anOrderItem()
                        .amount(Price(3000))
                        .build(),
                ),
            )

            // act
            order.cancel()

            // assert
            assertThat(order.orderStatus).isEqualTo(OrderStatusType.CANCELED)
        }

        @DisplayName("주문을 실패 처리하면 상태는 FAILED가 된다")
        @Test
        fun canChangeOrderToFailedStatus() {
            // arrange
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .build()

            order.addItems(
                listOf(
                    anOrderItem()
                        .amount(Price(2000))
                        .build(),
                    anOrderItem()
                        .amount(Price(3000))
                        .build(),
                ),
            )

            // act
            order.fail()

            // assert
            assertThat(order.orderStatus).isEqualTo(OrderStatusType.FAILED)
        }

        @DisplayName("주문 상태가 COMPLETED인 경우 isCompleted() 메서드는 true를 반환한다.")
        @Test
        fun isCompletedReturnsTrueWhenOrderStatusIsCompleted() {
            // arrange
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .build()

            order.addItems(
                listOf(
                    anOrderItem()
                        .amount(Price(2000))
                        .build(),
                    anOrderItem()
                        .amount(Price(3000))
                        .build(),
                ),
            )

            // act
            order.complete()

            // assert
            assertThat(order.isCompleted()).isTrue
        }

        @DisplayName("주문 상태가 COMPLETED가 아닌 경우 isCompleted() 메서드는 false를 반환한다.")
        @Test
        fun isCompletedReturnsFalseWhenOrderStatusIsNotCompleted() {
            // arrange
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .build()

            order.addItems(
                listOf(
                    anOrderItem()
                        .amount(Price(2000))
                        .build(),
                    anOrderItem()
                        .amount(Price(3000))
                        .build(),
                ),
            )

            // act
            order.cancel()

            // assert
            assertThat(order.isCompleted()).isFalse
        }

        @DisplayName("주문 금액과 비교하려는 금액이 같으면 isNotEqualAmount() 메서드는 false를 반환한다.")
        @Test
        fun isNotEqualAmountReturnsFalseWhenOrderAmountEqualsGivenPrice() {
            // arrange
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .build()

            order.addItems(
                listOf(
                    anOrderItem()
                        .amount(Price(2000))
                        .build(),
                    anOrderItem()
                        .amount(Price(3000))
                        .build(),
                ),
            )

            // act
            val isNotEqual = order.isNotEqualAmount(Price(5000))

            // assert
            assertThat(isNotEqual).isFalse
        }

        @DisplayName("주문 금액과 비교하려는 금액이 다르면 isNotEqualAmount() 메서드는 true를 반환한다.")
        @Test
        fun isNotEqualAmountReturnsTrueWhenOrderAmountDoesNotEqualGivenPrice() {
            // arrange
            val order = anOrder()
                .orderCustomer(anOrderCustomer().build())
                .build()

            order.addItems(
                listOf(
                    anOrderItem()
                        .amount(Price(2000))
                        .build(),
                    anOrderItem()
                        .amount(Price(3000))
                        .build(),
                ),
            )

            // act
            val isNotEqual = order.isNotEqualAmount(Price(6000))

            // assert
            assertThat(isNotEqual).isTrue
        }
    }
}
