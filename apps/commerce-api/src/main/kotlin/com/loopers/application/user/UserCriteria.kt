package com.loopers.application.user

import com.loopers.domain.user.UserCommand
import com.loopers.support.enums.user.GenderType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

class UserCriteria {
    data class SignUp(
        val userId: String,
        val name: String,
        val email: String,
        val birthday: String,
        val gender: GenderType,
    ) {

        init {
            (userId.length > 10 || !userId.matches(USER_ID_PATTERN)) &&
                    throw IllegalArgumentException("ID는 영문 및 숫자 10자 이내여야 합니다.")

            !email.matches(EMAIL_PATTERN) &&
                    throw IllegalArgumentException("이메일 형식이 올바르지 않습니다.")

            !birthday.matches(BIRTH_DATE_PATTERN) &&
                    throw IllegalArgumentException("생년월일 형식이 올바르지 않습니다. (yyyy-MM-dd)")

            runCatching { LocalDate.parse(birthday, BIRTH_DATE_FORMATTER) }
                .onFailure { throw IllegalArgumentException("존재하지 않는 생년월일입니다. ($birthday)") }
        }

        fun toCommand(): UserCommand.Create = UserCommand.Create(
            userId,
            name,
            email,
            birthday,
            gender,
        )

        companion object {
            private val USER_ID_PATTERN = "^[a-zA-Z0-9]{1,10}$".toRegex()
            private val EMAIL_PATTERN = "^(?!.*\\.\\.)[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
            private val BIRTH_DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()
            private val BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT)
        }
    }
}
