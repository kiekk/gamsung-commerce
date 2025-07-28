package com.loopers.domain.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun save(userEntity: UserEntity): UserEntity {
        return userRepository.save(userEntity)
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserEntity? {
        return userRepository.findById(id)
    }

    @Transactional(readOnly = true)
    fun getUserByUserId(userId: String): UserEntity? {
        return userRepository.findByUserId(userId)
    }
}
