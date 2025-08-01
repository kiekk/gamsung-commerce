package com.loopers.application.user

import com.loopers.domain.user.UserCommand
import com.loopers.domain.vo.Birthday
import com.loopers.domain.vo.Email
import com.loopers.support.enums.user.GenderType

class UserCriteria {
    data class SignUp(
        val username: String,
        val name: String,
        val email: Email,
        val birthday: Birthday,
        val gender: GenderType,
    ) {

        init {
            (username.length > 10 || !username.matches(USERNAME_PATTERN)) &&
                    throw IllegalArgumentException("ID는 영문 및 숫자 10자 이내여야 합니다.")
        }

        fun toCommand(): UserCommand.Create = UserCommand.Create(
            username,
            name,
            email,
            birthday,
            gender,
        )

        companion object {
            private val USERNAME_PATTERN = "^[a-zA-Z0-9]{1,10}$".toRegex()
        }
    }
}
