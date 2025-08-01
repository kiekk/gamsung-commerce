package com.loopers.domain.user

import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.domain.vo.Birthday
import com.loopers.domain.vo.Email
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.infrastructure.user.UserRepositoryImpl
import com.loopers.support.enums.user.GenderType
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
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                Email("shyoon991@gmail.com"),
                Birthday("2000-01-01"),
                GenderType.M,
            )
            val spyUserRepository = spy(UserRepositoryImpl(userJpaRepository))
            val userServiceWithSpy = UserService(spyUserRepository)

            // act
            val userEntity = userServiceWithSpy.save(userSignUpCommand)

            // assert
            verify(spyUserRepository, times(1)).save(userEntity)
        }

        @DisplayName("이미 가입된 ID 로 회원가입 시도 시, 실패한다.")
        @Test
        fun failsToSignUp_whenUserIdAlreadyExists() {
            // arrange
            val userSignUpCommand = UserCommand.Create(
                "userId123",
                "soono",
                Email("shyoon991@gmail.com"),
                Birthday("2000-01-01"),
                GenderType.M,
            )
            userService.save(userSignUpCommand)

            // act
            val exception = assertThrows<CoreException> {
                userService.save(userSignUpCommand)
            }

            // assert
            assertThat(exception.errorType).isEqualTo(ErrorType.CONFLICT)
        }
    }

    /*
     **🔗 통합 테스트**

    - [ ]  해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.
    - [ ]  해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.
     */
    @DisplayName("내 정보를 조회할 때, ")
    @Nested
    inner class GetMyInfo {
        @DisplayName("해당 ID 의 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        fun returnsMyInfo_whenRequestingMyInfo() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())

            // act
            val myInfo = userService.findUserBy(createdUser.username)

            // assert
            assertThat(myInfo).isNotNull
            assertThat(myInfo?.username).isEqualTo(createdUser.username)
            assertThat(myInfo?.name).isEqualTo(createdUser.name)
            assertThat(myInfo?.email).isEqualTo(createdUser.email)
            assertThat(myInfo?.birthday).isEqualTo(createdUser.birthday)
            assertThat(myInfo?.gender).isEqualTo(createdUser.gender)
        }

        @DisplayName("해당 ID 의 회원이 존재하지 않을 경우, null 이 반환된다.")
        @Test
        fun returnsNull_whenUserDoesNotExist() {
            // arrange
            val nonExistentUserId = "nonExistentUser"

            // act
            val myInfo = userService.findUserBy(nonExistentUserId)

            // assert
            assertThat(myInfo).isNull()
        }
    }
}
