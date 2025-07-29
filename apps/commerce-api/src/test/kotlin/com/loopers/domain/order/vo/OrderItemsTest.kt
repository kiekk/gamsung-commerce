package com.loopers.domain.order.vo

import com.loopers.domain.order.OrderItemEntity
import com.loopers.domain.vo.Price
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderItemsTest {
    @DisplayName("주문 아이템 목록을 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("주문 아이템 목록의 총 가격을 계산한다.")
        @Test
        fun calculatesTotalPrice() {
            // arrange
            val item1 = OrderItemEntity(
                1L,
                "Product 1",
                Quantity(2),
                Price(1000L),
                Price(2000L),
            )
            val item2 = OrderItemEntity(
                2L,
                "Product 2",
                Quantity(1),
                Price(1500L),
                Price(1500L),
            )
            val items = listOf(item1, item2)

            // act
            val orderItems = OrderItems(items)

            // assert
            assertAll(
                { assertThat(orderItems.size()).isEqualTo(2) },
                { assertThat(orderItems.totalPrice()).isEqualTo(Price(2500L)) },
                { assertThat(orderItems.amount()).isEqualTo(Price(3500L)) },
            )
        }

        @DisplayName("주문 아이템 목록이 비어있으면 예외를 발생시킨다.")
        @Test
        fun failsToCreateOrderItems_whenItemsIsEmpty() {
            // arrange
            val items = emptyList<OrderItemEntity>()

            // act & assert
            val exception = assertThrows<IllegalArgumentException> {
                OrderItems(items)
            }

            assertAll(
                { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
                { assertThat(exception.message).isEqualTo("주문 항목은 비어 있을 수 없습니다.") },
            )
        }
    }
}
