package com.loopers.domain.point

import com.loopers.domain.point.PointEntityFixture.Companion.aPoint
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PointTest {
    /*
     **🧱 단위 테스트**

    - [ ]  0 이하의 정수로 포인트를 충전 시 실패한다.
     */
    @DisplayName("Point 객체를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("0 이하의 정수로 포인트를 충전 시 실패한다.")
        @ParameterizedTest
        @ValueSource(
            longs = [
                0,
                -1,
            ],
        )
        fun failWhenChargePointIsZeroOrLess(invalidPoint: Long) {
            // arrange
            val pointEntity = aPoint().build()

            // act
            val result = assertThrows<CoreException> {
                pointEntity.chargePoint(invalidPoint)
            }

            // assert
            assertThat(result.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }
    }
}
