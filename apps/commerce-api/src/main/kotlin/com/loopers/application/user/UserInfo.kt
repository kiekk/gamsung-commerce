package com.loopers.application.user

import com.loopers.domain.user.UserEntity
import com.loopers.domain.vo.Birthday
import com.loopers.domain.vo.Email
import com.loopers.support.enums.user.GenderType

class UserInfo(
    val userId: String,
    val name: String,
    val email: Email,
    val birthday: Birthday,
    val gender: GenderType,
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
