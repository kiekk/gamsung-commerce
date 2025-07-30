package com.loopers.application.user

import com.loopers.domain.user.UserEntity

class UserInfo(
    val userId: String,
    val name: String,
    val email: String,
    val birthday: String,
    val gender: UserEntity.GenderType,
) {

    companion object {
        fun from(userEntity: UserEntity): UserInfo {
            return UserInfo(
                userEntity.userId,
                userEntity.name,
                userEntity.email,
                userEntity.birthday,
                userEntity.gender,
            )
        }
    }
}
