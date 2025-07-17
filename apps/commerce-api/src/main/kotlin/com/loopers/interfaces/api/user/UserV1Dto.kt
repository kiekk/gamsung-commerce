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
            userId,
            name,
            email,
            birthday,
            SignUp.GenderRequest.valueOf(gender.name),
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
                info.userId,
                info.name,
                info.email,
                info.birthday,
                GenderResponse.valueOf(info.gender.name),
            )
        }
    }
}
