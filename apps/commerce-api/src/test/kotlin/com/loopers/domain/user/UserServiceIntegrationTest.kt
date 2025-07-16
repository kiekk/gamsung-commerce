package com.loopers.domain.user

import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.infrastructure.user.UserRepositoryImpl
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestConstructor

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class UserServiceIntegrationTest @Autowired constructor(
    private val userService: UserService,
    private val userJpaRepository: UserJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
    **🔗 통합 테스트**

    - [ ]  회원 가입시 User 저장이 수행된다. ( spy 검증 )
    - [ ]  이미 가입된 ID 로 회원가입 시도 시, 실패한다.
     */

    @DisplayName("회원 가입을 할 때, ")
    @Nested
    inner class SignUp {
        @DisplayName("회원 가입시 User 저장이 수행된다. ( spy 검증 )")
        @Test
        fun savesUser_whenSigningUp() {
            // arrange
            val userEntity = aUser().build()
            val spyUserRepository = spy(UserRepositoryImpl(userJpaRepository))
            val userServiceWithSpy = UserService(spyUserRepository)

            // act
            userServiceWithSpy.save(userEntity)

            // assert
            verify(spyUserRepository, times(1)).save(userEntity)
        }

        @DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다.")
        @Test
        fun failsToSignUp_whenUserIdAlreadyExists() {
            // arrange
            val existingUser = aUser().build()
            userService.save(existingUser)

            // act
            val exception = assertThrows<CoreException> {
                userService.save(existingUser)
            }

            // assert
            assertThat(exception.errorType).isEqualTo(ErrorType.CONFLICT)
        }
    }
}
