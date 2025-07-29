package com.loopers.domain.point

import com.loopers.application.point.PointCriteria
import com.loopers.application.point.PointFacade
import com.loopers.domain.point.PointEntityFixture.Companion.aPoint
import com.loopers.infrastructure.point.PointJpaRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PointServiceIntegrationTest @Autowired constructor(
    private val pointFacade: PointFacade,
    private val pointJpaRepository: PointJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
     **🔗 통합 테스트**

    - [ ]  해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.
    - [ ]  해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.
     */

    @DisplayName("포인트 조회를 할 때, ")
    @Nested
    inner class Get {
        @DisplayName("해당 ID 의 회원이 존재할 경우, 보유 포인트가 반환된다.")
        @Test
        fun returnsPoints_whenUserExists() {
            // arrange
            val pointEntity = pointJpaRepository.save(
                aPoint().build(),
            )

            // act
            val userPoints = pointFacade.getPoint(pointEntity.userId)

            // assert
            assertThat(userPoints).isNotNull
            assertThat(userPoints?.userId).isEqualTo(pointEntity.userId)
            assertThat(userPoints?.point).isEqualTo(pointEntity.point)
        }

        @Test
        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        fun returnsNull_whenUserDoesNotExist() {
            // arrange
            val nonExistentUserId = "non-existent-user-id"

            // act
            val userPoints = pointFacade.getPoint(nonExistentUserId)

            // assert
            assertThat(userPoints).isNull()
        }
    }

    /*
     **통합 테스트**

    - [ ]  존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.
     */
    @DisplayName("포인트 충전을 할 때, ")
    @Nested
    inner class Charge {
        @DisplayName("존재하지 않는 유저 ID 로 충전을 시도한 경우, 실패한다.")
        @Test
        fun failsToCharge_whenUserDoesNotExist() {
            // arrange
            val nonExistentUserId = "non-existent-user-id"
            val chargeAmount = 100L
            val pointChargeCriteria = PointCriteria.Charge(
                nonExistentUserId,
                chargeAmount,
            )

            // act
            val exception = assertThrows<CoreException> {
                pointFacade.chargePoint(pointChargeCriteria)
            }

            // assert
            assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND)
        }
    }
}
