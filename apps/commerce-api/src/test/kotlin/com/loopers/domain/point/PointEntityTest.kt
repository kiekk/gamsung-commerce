package com.loopers.domain.point

import com.loopers.domain.point.PointEntityFixture.Companion.aPoint
import com.loopers.domain.point.vo.Point
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PointEntityTest {
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
                pointEntity.chargePoint(Point(invalidPoint))
            }

            // assert
            assertThat(result.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }
    }

    /*
    **🧱 단위 테스트**
    - [ ] 포인트를 사용하면 포인트가 차감된다.
    - [ ] 포인트 사용이 가능하면 true를 반환한다.
    - [ ] 포인트 사용이 불가능하면 false를 반환한다.
    - [ ] 포인트를 환불하면 환불된 포인트만큼 포인트가 증가한다.
     */
    @DisplayName("Point 객체를 사용할 때, ")
    @Nested
    inner class Use {
        @DisplayName("포인트를 사용하면 포인트가 차감된다.")
        @ParameterizedTest
        @ValueSource(longs = [100, 200, 300])
        fun usePoint(pointToUse: Long) {
            // arrange
            val pointEntity = aPoint().point(Point(500)).build()

            // act
            pointEntity.usePoint(Point(pointToUse))

            // assert
            assertThat(pointEntity.point).isEqualTo(Point(500 - pointToUse))
        }

        @DisplayName("포인트 사용이 가능하면 true를 반환한다.")
        @ParameterizedTest
        @ValueSource(longs = [100, 200, 300])
        fun canUsePointWhenEnough(pointToUse: Long) {
            // arrange
            val pointEntity = aPoint().point(Point(500)).build()

            // act
            val result = !pointEntity.cannotUsePoint(Point(pointToUse))

            // assert
            assertThat(result).isTrue()
        }

        @DisplayName("포인트 사용이 불가능하면 false를 반환한다.")
        @ParameterizedTest
        @ValueSource(longs = [600, 700, 800])
        fun cannotUsePointWhenNotEnough(pointToUse: Long) {
            // arrange
            val pointEntity = aPoint().point(Point(500)).build()

            // act
            val result = pointEntity.cannotUsePoint(Point(pointToUse))

            // assert
            assertThat(result).isTrue()
        }

        @DisplayName("포인트를 환불하면 환불된 포인트만큼 포인트가 증가한다.")
        @ParameterizedTest
        @ValueSource(longs = [100, 200, 300])
        fun refundPoint(refundAmount: Long) {
            // arrange
            val pointEntity = aPoint().point(Point(500)).build()

            // act
            pointEntity.refundPoint(Point(refundAmount))

            // assert
            assertThat(pointEntity.point).isEqualTo(Point(500 + refundAmount))
        }
    }
}
