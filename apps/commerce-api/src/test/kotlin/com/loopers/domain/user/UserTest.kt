package com.loopers.domain.user

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UserTest {
    /*
    **🧱 단위 테스트**
    - [ ]  ID 가 `영문 및 숫자 10자 이내` 형식에 맞지 않으면, User 객체 생성에 실패한다.
    - [ ]  이메일이 `xx@yy.zz` 형식에 맞지 않으면, User 객체 생성에 실패한다.
    - [ ]  생년월일이 `yyyy-MM-dd` 형식에 맞지 않으면, User 객체 생성에 실패한다.
     */
    @DisplayName("User 객체를 생성할 때, ")
    @Nested
    inner class Create {

        @DisplayName("ID 가 `영문 및 숫자 10자 이내` 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @Test
        fun failsToCreateUser_whenIdIsInvalid() {
            // arrange
            val userId = "invalid_id_1234567890" // 20자 이상

            // act
            val result = assertThrows<CoreException> {
                UserEntity(
                    userId = userId,
                    email = "shyoon991@gmail.com",
                    birthday = "1999-09-01",
                    genderType = UserEntity.GenderType.M,
                )
            }

            // assert
            assertThat(result.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("이메일이 `xx@yy.zz` 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @Test
        fun failsToCreateUser_whenEmailIsInvalid() {
            // arrange
            val email = "invalid_email_format" // 잘못된 이메일 형식

            // act
            val result = assertThrows<CoreException> {
                UserEntity(
                    userId = "shyoon991",
                    email = email,
                    birthday = "1999-09-01",
                    genderType = UserEntity.GenderType.M,
                )
            }

            // assert
            assertThat(result.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

        @DisplayName("생년월일이 `yyyy-MM-dd` 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @Test
        fun failsToCreateUser_whenBirthDateIsInvalid() {
            // arrange
            val birthday = "1999/09/01" // 잘못된 날짜 형식

            // act
            val result = assertThrows<CoreException> {
                UserEntity(
                    userId = "shyoon991",
                    email = "shyoon991@gmail.com",
                    birthday = birthday,
                    genderType = UserEntity.GenderType.M,
                )
            }

            // assert
            assertThat(result.errorType).isEqualTo(ErrorType.BAD_REQUEST)
        }

    }
}
