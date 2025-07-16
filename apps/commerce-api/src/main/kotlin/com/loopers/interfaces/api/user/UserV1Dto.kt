package com.loopers.interfaces.api.user

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
    }
}
