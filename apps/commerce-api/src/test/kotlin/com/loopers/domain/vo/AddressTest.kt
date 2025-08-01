package com.loopers.domain.vo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AddressTest {
    @DisplayName("우편번호가 올바르지 않으면 Address 객체 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // 빈 문자열인 경우
            "1234", // 4자리 숫자인 경우
            "123456", // 6자리 숫자인 경우
            "56a78", // 숫자가 아닌 문자가 포함된 경우
        ],
    )
    fun failsToCreateAddress_whenPostalCodeIsInvalid(invalidPostalCode: String) {
        // act & assert
        val exception = assertThrows<IllegalArgumentException> {
            Address(invalidPostalCode, "서울시 강남구 역삼동", "테스트 도로 123")
        }

        assertAll(
            { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
            { assertThat(exception.message).isEqualTo("우편번호는 5자리 숫자여야 합니다.") },
        )
    }

    @DisplayName("주소 형식이 올바르지 않으면 Address 객체 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // 빈 문자열인 경우
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", // 101자 이상인 경우
            "서울시 강남구 역삼동!@#", // 특수문자가 포함된 경우
        ],
    )
    fun failsToCreateAddress_whenAddressIsInvalid(invalidAddress: String) {
        // act & assert
        val exception = assertThrows<IllegalArgumentException> {
            Address("12345", invalidAddress, "테스트 도로 123")
        }

        assertAll(
            { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
            { assertThat(exception.message).isEqualTo("주소 형식이 올바르지 않습니다.") },
        )
    }

    @DisplayName("상세주소 형식이 올바르지 않으면 Address 객체 생성에 실패한다.")
    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // 빈 문자열인 경우
            "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", // 101자 이상인 경우
            "테스트 도로 123!@#", // 특수문자가 포함된 경우
        ],
    )
    fun failsToCreateAddress_whenAddressDetailIsInvalid(invalidAddressDetail: String) {
        // act & assert
        val exception = assertThrows<IllegalArgumentException> {
            Address("12345", "서울시 강남구 역삼동", invalidAddressDetail)
        }

        assertAll(
            { assertThat(exception).isInstanceOf(IllegalArgumentException::class.java) },
            { assertThat(exception.message).isEqualTo("상세주소 형식이 올바르지 않습니다.") },
        )
    }
}
