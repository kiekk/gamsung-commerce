package com.loopers.domain.user

interface UserRepository {
    fun save(userEntity: UserEntity): UserEntity
}
