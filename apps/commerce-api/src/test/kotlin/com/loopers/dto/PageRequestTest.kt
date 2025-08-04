package com.loopers.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageRequest

class PageRequestTest {
    /*
     **🔗 통합 테스트
    - [ ] 페이지 번호가 음수일 경우 예외가 발생한다.
    - [ ] 페이지 크기가 0 이하일 경우 예외가 발생한다.
     */
    @DisplayName("페이지 번호가 음수일 경우 예외가 발생한다.")
    @Test
    fun failsToSearchBrand_whenPageNumberIsNegative() {
        // arrange

        // act
        val exception = assertThrows<IllegalArgumentException> {
            PageRequest.of(-1, 10)
        }

        // assert
        assertAll(
            { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
        )
    }

    @DisplayName("페이지 크기가 0 이하일 경우 예외가 발생한다.")
    @Test
    fun failsToSearchBrand_whenPageSizeIsZeroOrLess() {
        // arrange

        // act
        val exception = assertThrows<IllegalArgumentException> {
            PageRequest.of(0, 0)
        }

        // assert
        assertAll(
            { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
        )
    }
}
