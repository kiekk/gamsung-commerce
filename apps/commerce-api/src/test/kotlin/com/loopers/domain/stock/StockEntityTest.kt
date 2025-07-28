package com.loopers.domain.stock

import com.loopers.domain.stock.StockEntityFixture.Companion.aStock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows

class StockEntityTest {

    /*
     * 🧱 단위 테스트
    - [ ] 재고가 음수일 경우 Stock 생성에 실패한다.
    - [ ] 재고가 0 이상인 경우, Stock를 생성한다.
     */
    @DisplayName("재고를 생성할 떄, ")
    @Nested
    inner class Create {
        @DisplayName("재고가 음수일 경우 Stock 생성에 실패한다.")
        @Test
        fun failsToCreateStock_whenValueIsNegative() {
            // arrange
            val productId = 1L
            val invalidStockValue = -1

            // act & assert
            val exception = assertThrows<IllegalArgumentException> {
                StockEntity(productId, invalidStockValue)
            }

            assertAll(
                { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
                { assertThat(exception.message).isEqualTo("재고는 0 이상이어야 합니다.") },
            )
        }

        @DisplayName("재고가 0 이상인 경우, Stock를 생성한다.")
        @Test
        fun createsStock_whenValueIsValid() {
            // arrange
            val productId = 1L
            val validStockValue = 10

            // act
            val stockEntity = StockEntity(productId, validStockValue)

            // assert
            assertThat(stockEntity.quantity).isEqualTo(validStockValue)
        }
    }

    /*
    * 🧱 단위 테스트
    - [ ] 재고의 수량이 차감할 재고 수량보다 작으면 true를 반환한다.
    - [ ] 재고의 수량이 차감할 재고 수량보다 크거나 같으면 false를 반환한다.
    - [ ] 재고가 차감할 수량보다 적을 경우, 차감에 실패한다.
    - [ ] 재고가 차감할 수량 이상인 경우, 재고를 차감한다.
    */
    @DisplayName("재고를 차감할 때, ")
    @Nested
    inner class Deduct {

        @DisplayName("재고의 수량이 차감할 재고 수량보다 작으면 true를 반환한다.")
        @Test
        fun checksIfStockIsLessThanDeductAmount_whenValueIsLessThanDeductAmount() {
            // arrange
            val initialStockValue = 1
            val stockEntity = aStock().quantity(initialStockValue).build()
            val deductAmount = 2

            // act
            val result = stockEntity.isQuantityLessThan(deductAmount)

            // assert
            assertThat(result).isTrue()
        }

        @DisplayName("재고의 수량이 차감할 재고 수량보다 크거나 같으면 false를 반환한다.")
        @Test
        fun checksIfStockIsGreaterThanOrEqualToDeductAmount_whenValueIsGreaterThan() {
            // arrange
            val initialStockValue = 1
            val stockEntity = aStock().quantity(initialStockValue).build()
            val deductAmount = 1

            // act
            val result = stockEntity.isQuantityLessThan(deductAmount)

            // assert
            assertThat(result).isFalse()
        }

        @DisplayName("재고가 차감할 수량보다 적을 경우, 차감에 실패한다.")
        @Test
        fun failsToDeductStock_whenValueIsLessThanDeductAmount() {
            // arrange
            val initialStockValue = 1
            val stockEntity = aStock().quantity(initialStockValue).build()
            val deductQuantity = 2

            // act & assert
            val exception = assertThrows<IllegalArgumentException> {
                stockEntity.deductQuantity(deductQuantity)
            }

            assertAll(
                { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
                { assertThat(exception.message).isEqualTo("차감할 재고 수량이 없습니다.") },
            )
        }

        @DisplayName("재고가 차감할 수량 이상인 경우, 재고를 차감한다.")
        @Test
        fun deductsStock_whenValueIsGreaterThanOrEqualToDeductAmount() {
            // arrange
            val initialStockValue = 2
            val stockEntity = aStock().quantity(initialStockValue).build()
            val deductQuantity = 1

            // act
            stockEntity.deductQuantity(deductQuantity)

            // assert
            assertThat(stockEntity.quantity).isEqualTo(initialStockValue - deductQuantity)
        }

    }
}
