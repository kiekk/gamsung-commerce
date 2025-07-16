package com.loopers.application.user

import com.loopers.domain.user.UserEntity

class UserInfo(
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
        fun from(userEntity: UserEntity): UserInfo {
            return UserInfo(
                userId = userEntity.userId,
                name = userEntity.name,
                email = userEntity.email,
                birthday = userEntity.birthday,
                gender = GenderResponse.valueOf(userEntity.gender.name),
            )
        }
    }
}
