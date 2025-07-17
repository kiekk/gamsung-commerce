package com.loopers.interfaces.api.user

import com.loopers.application.user.SignUp
import com.loopers.application.user.UserInfo
import org.jetbrains.annotations.NotNull

class UserV1Dto {

    data class SignUpRequest(
        @NotNull
        val userId: String,
        @NotNull
        val name: String,
        @NotNull
        val email: String,
        @NotNull
        val birthday: String,
        @NotNull
        val gender: GenderRequest,
    ) {
        fun toSignUp(): SignUp = SignUp(
            userId = userId,
            name = name,
            email = email,
            birthday = birthday,
            gender = SignUp.GenderRequest.valueOf(gender.name),
        )

        enum class GenderRequest {
            M,
            F,
        }
    }

    data class UserResponse(
        val userId: String,
        val name: String,
        val email: String,
        val birthday: String,
        val gender: GenderResponse,
    ) {
        enum class GenderResponse {
            M,
            F,
        }


        companion object {
            fun from(info: UserInfo): UserResponse = UserResponse(
                userId = info.userId,
                name = info.name,
                email = info.email,
                birthday = info.birthday,
                gender = GenderResponse.valueOf(info.gender.name),
            )
        }
    }
}
