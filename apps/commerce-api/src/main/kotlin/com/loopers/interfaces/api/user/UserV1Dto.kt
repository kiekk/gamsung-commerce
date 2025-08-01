package com.loopers.interfaces.api.user

import com.loopers.application.user.UserCriteria
import com.loopers.application.user.UserInfo
import com.loopers.support.enums.user.GenderType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.jetbrains.annotations.NotNull

class UserV1Dto {
    data class SignUpRequest(
        @field:NotBlank
        val userId: String,
        @field:NotBlank
        val name: String,
        @field:NotBlank
        val email: String,
        @field:NotBlank
        @field:Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생일은 YYYY-MM-DD 형식이어야 합니다.")
        val birthday: String,
        @field:NotNull
        val gender: GenderType,
    ) {
        fun toSignUp(): UserCriteria.SignUp = UserCriteria.SignUp(
            userId,
            name,
            email,
            birthday,
            gender,
        )
    }

    data class UserResponse(
        val userId: String,
        val name: String,
        val email: String,
        val birthday: String,
        val gender: GenderType,
    ) {
        companion object {
            fun from(info: UserInfo): UserResponse = UserResponse(
                info.userId,
                info.name,
                info.email,
                info.birthday,
                info.gender,
            )
        }
    }
}
