package com.loopers.domain.vo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class MobileTest {
    @DisplayName("전화번호 형식이 올바르지 않으면 Mobile 객체 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // 빈 문자열인 경우
            "1234567890", // 하이픈 없이 숫자만 있는 경우
            "123-456-7890", // 하이픈 위치가 잘못된 경우
            "123-4567-890", // 하이픈 위치가 잘못된 경우
            "123-456-78901", // 숫자가 너무 많은 경우
            "123-45a-6789", // 숫자가 아닌 문자가 포함된 경우
        ],
    )
    fun failsToCreateMobile_whenFormatIsInvalid(invalidMobile: String) {
        // act & assert
        val exception = assertThrows<IllegalArgumentException> {
            Mobile(invalidMobile)
        }

        assertAll(
            { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
            { assertThat(exception.message).isEqualTo("전화번호 형식이 올바르지 않습니다.") },
        )
    }
}
