package com.loopers.domain.order

import com.loopers.domain.order.vo.Quantity
import com.loopers.domain.vo.Price
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderItemEntityTest {
    @DisplayName("주문 아이템 엔티티를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("상품 아이디가 비어있으면 예외를 발생시킨다.")
        @Test
        fun failsToCreateOrderItem_whenProductIdIsEmpty() {
            // arrange
            val productId = 0L
            val productName = "Test Product"
            val quantity = Quantity(1)
            val price = Price(1000L)
            val totalPrice = Price(1000L)

            // act
            val exception = assertThrows<IllegalArgumentException> {
                OrderItemEntity(
                    productId,
                    productName,
                    quantity,
                    price,
                    totalPrice,
                )
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
                { assertThat(exception.message).isEqualTo("상품 아이디는 0보다 커야 합니다.") },
            )
        }

        @DisplayName("상품 이름이 비어있으면 예외를 발생시킨다.")
        @Test
        fun failsToCreateOrderItem_whenProductNameIsEmpty() {
            // arrange
            val productId = 1L
            val productName = ""
            val quantity = Quantity(1)
            val price = Price(1000L)
            val totalPrice = Price(1000L)

            // act
            val exception = assertThrows<IllegalArgumentException> {
                OrderItemEntity(
                    productId,
                    productName,
                    quantity,
                    price,
                    totalPrice,
                )
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
                { assertThat(exception.message).isEqualTo("상품 이름은 비어있을 수 없습니다.") },
            )
        }
    }
}
