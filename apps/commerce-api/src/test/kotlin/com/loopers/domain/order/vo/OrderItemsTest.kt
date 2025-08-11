package com.loopers.domain.order.vo

import com.loopers.domain.order.OrderItemEntity
import com.loopers.domain.order.fixture.OrderItemEntityFixture.Companion.anOrderItem
import com.loopers.domain.vo.Price
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OrderItemsTest {
    /*
     * 🧱 단위 테스트
    - [ ] 주문 아이템 목록을 생성할 때, 총 가격을 계산한다.
    - [ ] 주문 아이템 목록이 비어있으면 총 가격은 0이다.
     */
    @DisplayName("주문 아이템 목록을 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("주문 아이템 목록의 총 가격을 계산한다.")
        @Test
        fun calculatesTotalPrice() {
            // arrange
            val items = listOf(
                anOrderItem()
                    .productId(1L)
                    .productName("Product 1")
                    .amount(Price(1000L))
                    .build(),
                anOrderItem()
                    .productId(2L)
                    .productName("Product 2")
                    .amount(Price(1500L))
                    .build(),
            )

            // act
            val orderItems = OrderItems(items)

            // assert
            assertAll(
                { assertThat(orderItems.size()).isEqualTo(2) },
                { assertThat(orderItems.amount()).isEqualTo(Price(2500L)) },
            )
        }

        @DisplayName("주문 아이템 목록이 비어있으면 총 가격은 0이다.")
        @Test
        fun calculatesTotalPrice_whenItemsAreEmpty() {
            // arrange
            val items = emptyList<OrderItemEntity>()

            // act & assert
            val orderItems = OrderItems(items)

            assertAll(
                { assertThat(orderItems.size()).isZero() },
                { assertThat(orderItems.amount()).isEqualTo(Price.ZERO) },
            )
        }
    }
}
