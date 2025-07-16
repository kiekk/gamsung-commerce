package com.loopers.domain.user

import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun save(userEntity: UserEntity): UserEntity {
        return userRepository.save(userEntity)
    }
}
