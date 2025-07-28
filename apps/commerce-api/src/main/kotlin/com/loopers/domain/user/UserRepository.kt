package com.loopers.domain.user

interface UserRepository {
    fun save(userEntity: UserEntity): UserEntity

    fun findByUserId(userId: String): UserEntity?

    fun findById(id: Long): UserEntity?
}
