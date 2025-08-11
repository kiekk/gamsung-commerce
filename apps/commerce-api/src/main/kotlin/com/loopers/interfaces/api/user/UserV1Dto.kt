package com.loopers.interfaces.api.user

import com.loopers.application.user.UserCriteria
import com.loopers.application.user.UserInfo
import com.loopers.domain.vo.Birthday
import com.loopers.domain.vo.Email
import com.loopers.support.enums.user.GenderType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.jetbrains.annotations.NotNull

class UserV1Dto {
    data class SignUpRequest(
        @field:NotBlank
        val username: String,
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
            username,
            name,
            Email(email),
            Birthday(birthday),
            gender,
        )
    }

    data class UserResultResponse(
        val username: String,
        val name: String,
        val email: String,
        val birthday: String,
        val gender: GenderType,
    ) {
        companion object {
            fun from(userResult: UserInfo.UserResult): UserResultResponse = UserResultResponse(
                userResult.username,
                userResult.name,
                userResult.email,
                userResult.birthday,
                userResult.gender,
            )
        }
    }

    data class UserDetailResponse(
        val username: String,
        val name: String,
        val email: String,
        val birthday: String,
        val gender: GenderType,
    ) {
        companion object {
            fun from(userDetail: UserInfo.UserDetail): UserDetailResponse = UserDetailResponse(
                userDetail.username,
                userDetail.name,
                userDetail.email,
                userDetail.birthday,
                userDetail.gender,
            )
        }
    }
}
