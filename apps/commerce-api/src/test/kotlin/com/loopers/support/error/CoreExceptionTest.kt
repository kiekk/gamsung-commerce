package com.loopers.support.error

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class CoreExceptionTest {
    @DisplayName("ErrorType 기반의 예외 생성 시, 별도의 메시지가 주어지지 않으면 ErrorType의 메시지를 사용한다.")
    @Test
    fun messageShouldBeErrorTypeMessage_whenCustomMessageIsNull() {
        // arrange
        val errorTypes = ErrorType.entries

        // act
        errorTypes.forEach { errorType ->
            val exception = CoreException(errorType)

            // assert
            assertThat(exception.message).isEqualTo(errorType.message)
        }
    }

    @DisplayName("ErrorType 기반의 예외 생성 시, 별도의 메시지가 주어지면 해당 메시지를 사용한다.")
    @Test
    fun messageShouldBeCustomMessage_whenCustomMessageIsNotNull() {
        // arrange
        val customMessage = "custom message"

        // act
        val exception = CoreException(ErrorType.INTERNAL_ERROR, customMessage)

        // assert
        assertThat(exception.message).isEqualTo(customMessage)
    }
}
