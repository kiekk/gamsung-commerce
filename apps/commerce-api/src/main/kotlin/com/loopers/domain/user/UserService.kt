package com.loopers.domain.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    @Transactional
    fun save(command: UserCommand.Create): UserEntity {
        return userRepository.save(command.toEntity())
    }

    @Transactional(readOnly = true)
    fun findUserBy(id: Long): UserEntity? {
        return userRepository.findById(id)
    }

    @Transactional(readOnly = true)
    fun findUserBy(userId: String): UserEntity? {
        return userRepository.findByUserId(userId)
    }
}
