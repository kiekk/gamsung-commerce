package com.loopers.application.user

import com.loopers.domain.user.UserEntity
import com.loopers.support.enums.user.GenderType

class UserInfo {
    data class UserResult(
        val id: Long,
        val username: String,
        val name: String,
        val email: String,
        val birthday: String,
        val gender: GenderType,
    ) {
        companion object {
            fun from(userEntity: UserEntity): UserResult {
                return UserResult(
                    userEntity.id,
                    userEntity.username,
                    userEntity.name,
                    userEntity.email.value,
                    userEntity.birthday.value,
                    userEntity.gender,
                )
            }
        }
    }

    data class UserDetail(
        val id: Long,
        val username: String,
        val name: String,
        val email: String,
        val birthday: String,
        val gender: GenderType,
    ) {
        companion object {
            fun from(userEntity: UserEntity): UserDetail {
                return UserDetail(
                    userEntity.id,
                    userEntity.username,
                    userEntity.name,
                    userEntity.email.value,
                    userEntity.birthday.value,
                    userEntity.gender,
                )
            }
        }
    }
}
