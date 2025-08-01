package com.loopers.domain.vo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PriceTest {

    /*
     **🧱 단위 테스트
     - [ ] 가격이 음수일 경우 Price 생성에 실패한다.
     - [ ] 가격이 0 이상인 경우, Price를 생성한다.
     */

    @DisplayName("가격을 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("가격이 음수일 경우 Price 객체 생성에 실패한다.")
        @Test
        fun failsToCreatePrice_whenValueIsNegative() {
            // arrange
            val invalidPriceValue = -1L

            // act & assert
            val exception = assertThrows<IllegalArgumentException> {
                Price(invalidPriceValue)
            }

            assertAll(
                { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
                { assertThat(exception.message).isEqualTo("가격은 0 이상이어야 합니다.") },
            )
        }

        @DisplayName("가격이 0 이상인 경우, Price 객체를 생성한다.")
        @Test
        fun createsPrice_whenValueIsValid() {
            // arrange
            val validPriceValue = 100L

            // act
            val price = Price(validPriceValue)

            // assert
            assertThat(price.value).isEqualTo(validPriceValue)
        }
    }
}
