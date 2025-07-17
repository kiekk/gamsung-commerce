package com.loopers.application.user

import com.loopers.domain.user.UserEntity

class SignUp(
    val userId: String,
    val name: String,
    val email: String,
    val birthday: String,
    val gender: GenderRequest,
) {
    enum class GenderRequest {
        M,
        F,
    }

    fun toUserEntity(): UserEntity = UserEntity(
        userId = userId,
        name = name,
        email = email,
        birthday = birthday,
        gender = UserEntity.GenderType.valueOf(gender.name),
    )
}
