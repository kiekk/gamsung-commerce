package com.loopers.application.user

import com.loopers.domain.user.UserEntity
import com.loopers.support.enums.user.GenderType

class UserInfo(
    val username: String,
    val name: String,
    val email: String,
    val birthday: String,
    val gender: GenderType,
) {

    companion object {
        fun from(userEntity: UserEntity): UserInfo {
            return UserInfo(
                userEntity.username,
                userEntity.name,
                userEntity.email.value,
                userEntity.birthday.value,
                userEntity.gender,
            )
        }
    }
}
