package com.loopers.domain.order.vo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class QuantityTest {
    @DisplayName("수량이 0 이하인 경우 Quantity 객체 생성에 실패한다.")
    @Test
    fun failsToCreateQuantity_whenValueIsZeroOrNegative() {
        // arrange
        val invalidValues = listOf(0, -1)

        // act & assert
        invalidValues.forEach { value ->
            val exception = assertThrows<IllegalArgumentException> {
                Quantity(value)
            }
            assertEquals("수량은 1 이상이어야 합니다.", exception.message)
        }
    }

    @DisplayName("수량이 1 이상인 경우 Quantity 객체를 생성한다.")
    @Test
    fun createsQuantity_whenValueIsPositive() {
        // arrange
        val validValue = 5

        // act
        val quantity = Quantity(validValue)

        // assert
        assertEquals(validValue, quantity.value)
    }
}
