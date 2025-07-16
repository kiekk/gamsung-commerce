package com.loopers.interfaces.api.user

class UserV1Dto {

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
