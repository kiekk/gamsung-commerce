package com.loopers.domain.order.vo

import com.loopers.domain.order.OrderItemEntity
import com.loopers.domain.order.OrderItemEntityFixture.Companion.anOrderItem
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
            val items = listOf(
                anOrderItem()
                    .productId(1L)
                    .productName("Product 1")
                    .quantity(Quantity(2))
                    .price(Price(1000L))
                    .totalPrice(Price(2000L))
                    .build(),
                anOrderItem()
                    .productId(2L)
                    .productName("Product 2")
                    .quantity(Quantity(1))
                    .price(Price(1500L))
                    .totalPrice(Price(1500L))
                    .build(),
            )

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
