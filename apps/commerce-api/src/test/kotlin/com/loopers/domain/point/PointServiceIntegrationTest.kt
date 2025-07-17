package com.loopers.domain.point

import com.loopers.application.point.PointFacade
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.domain.user.UserService
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PointServiceIntegrationTest @Autowired constructor(
    private val userService: UserService,
    private val pointFacade: PointFacade,
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
            // TODO: userFacade로 변경 예정
            val userEntity = userService.save(aUser().build())

            // act
            val userPoints = pointFacade.getUserPoints(userEntity.userId)

            // assert
            assertThat(userPoints).isNotNull
            assertThat(userPoints.userId).isEqualTo(userEntity.userId)
            assertThat(userPoints.point).isGreaterThanOrEqualTo(0L)
        }

        @Test
        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        fun returnsNull_whenUserDoesNotExist() {
            // arrange
            val nonExistentUserId = "non-existent-user-id"

            // act
            val userPoints = pointFacade.getUserPoints(nonExistentUserId)

            // assert
            assertThat(userPoints).isNull()
        }
    }
}
