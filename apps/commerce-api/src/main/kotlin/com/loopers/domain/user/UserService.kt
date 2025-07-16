package com.loopers.domain.user

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun save(userEntity: UserEntity): UserEntity {
        return userRepository.save(userEntity)
    }
}
