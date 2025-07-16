package com.loopers.infrastructure.user

import com.loopers.domain.user.UserEntity
import com.loopers.domain.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl : UserRepository {
    override fun save(userEntity: UserEntity): UserEntity {
        TODO("Not yet implemented")
    }
}
