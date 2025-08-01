package com.loopers.infrastructure.user

import com.loopers.domain.user.UserEntity
import com.loopers.domain.user.UserRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun save(userEntity: UserEntity): UserEntity {
        userJpaRepository.findByUsername(userEntity.username)?.let {
            throw CoreException(
                ErrorType.CONFLICT,
                "이미 존재하는 사용자입니다: ${userEntity.username}",
            )
        }
        return userJpaRepository.save(userEntity)
    }

    override fun findByUsername(username: String): UserEntity? {
        return userJpaRepository.findByUsername(username)
    }

    override fun findById(id: Long): UserEntity? {
        return userJpaRepository.findById(id).orElse(null)
    }
}
