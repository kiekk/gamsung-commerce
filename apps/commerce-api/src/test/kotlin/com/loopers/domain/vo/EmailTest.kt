package com.loopers.domain.vo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class EmailTest {
    @DisplayName("이메일 형식이 올바르지 않으면 Email 객체 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // 빈 문자열인 경우
            "invalid-email", // 형식이 잘못된 경우
            "user@domain..com", // 도메인 부분에 연속된 점이 있는 경우
            "user@.com", // 도메인 부분이 비어있는 경우
            "@domain.com", // 사용자 부분이 비어있는 경우
        ],
    )
    fun failsToCreateEmail_whenFormatIsInvalid(invalidEmail: String) {
        // act & assert
        val exception = assertThrows<IllegalArgumentException> {
            Email(invalidEmail)
        }

        assertAll(
            { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
            { assertThat(exception.message).isEqualTo("이메일 형식이 올바르지 않습니다.") },
        )
    }
}
