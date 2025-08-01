package com.loopers.domain.order

import com.loopers.domain.order.OrderItemEntityFixture.Companion.anOrderItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderItemEntityTest {
    /*
     * 🧱 단위 테스트
     - [ ] 상품 아이디가 비어있으면 예외를 발생시킨다.
     - [ ] 상품 이름이 비어있으면 예외를 발생시킨다.
     */
    @DisplayName("주문 아이템 엔티티를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("상품 아이디가 비어있으면 예외를 발생시킨다.")
        @Test
        fun failsToCreateOrderItem_whenProductIdIsEmpty() {
            // arrange
            val invalidProductId = 0L

            // act
            val exception = assertThrows<IllegalArgumentException> {
                anOrderItem()
                    .productId(invalidProductId)
                    .build()
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
            val invalidProductName = ""

            // act
            val exception = assertThrows<IllegalArgumentException> {
                anOrderItem()
                    .productName(invalidProductName)
                    .build()
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
                { assertThat(exception.message).isEqualTo("상품 이름은 비어있을 수 없습니다.") },
            )
        }
    }
}
