package com.loopers.domain.user

interface UserRepository {
    fun save(userEntity: UserEntity): UserEntity

    fun findByUsername(username: String): UserEntity?

    fun findById(id: Long): UserEntity?
}
