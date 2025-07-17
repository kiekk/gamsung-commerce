package com.loopers.domain.user

import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

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
        @ParameterizedTest
        @ValueSource(
            strings = [
                "수수수수", // 한글이 포함된 경우
                "abcdefghij1", // 길이가 11인 경우
                "abc!@#", // 특수문자가 포함된 경우
                "abc def", // 공백이 포함된 경우
            ],
        )
        fun failsToCreateUser_whenIdIsInvalid(invalidUserId: String) {
            // arrange

            // act
            val result = assertThrows<IllegalArgumentException> {
                aUser().userId(invalidUserId).build()
            }

            // assert
            assertThat(result).isInstanceOf(IllegalArgumentException::class.java)
        }

        @DisplayName("이메일이 `xx@yy.zz` 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(
            strings = [
                "invalid-email", // 형식이 잘못된 경우
                "user@domain", // 도메인이 없는 경우
                "@domain.com", // 사용자 이름이 없는 경우
                "user@.com", // 도메인 부분이 잘못된 경우
                "user@domain..com", // 도메인 부분에 점이 연속된 경우
                "user..name@example.com", // 사용자 이름 부분에 점이 연속된 경우
            ],
        )
        fun failsToCreateUser_whenEmailIsInvalid(invalidEmail: String) {
            // arrange

            // act
            val result = assertThrows<IllegalArgumentException> {
                aUser().email(invalidEmail).build()
            }

            // assert
            assertThat(result).isInstanceOf(IllegalArgumentException::class.java)
        }

        @DisplayName("생년월일이 `yyyy-MM-dd` 형식에 맞지 않으면, User 객체 생성에 실패한다.")
        @ParameterizedTest
        @ValueSource(
            strings = [
                "2023/02/28", // 잘못된 구분자
                "2023-2-28", // 월이 한 자리 숫자인 경우
                "2023-02-8", // 일이 한 자리 숫자인 경우
                "20230228", // 형식이 잘못된 경우
                "2023-02-30", // 존재하지 않는 날짜
            ],
        )
        fun failsToCreateUser_whenBirthDateIsInvalid(invalidBirthday: String) {
            // arrange

            // act
            val result = assertThrows<IllegalArgumentException> {
                aUser().birthday(invalidBirthday).build()
            }

            // assert
            assertThat(result).isInstanceOf(IllegalArgumentException::class.java)
        }

    }
}
