package com.loopers.infrastructure.user

import com.loopers.domain.user.UserEntity
import com.loopers.domain.user.UserRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userMap: MutableMap<String, UserEntity> = mutableMapOf(),
) : UserRepository {
    override fun save(userEntity: UserEntity): UserEntity {
        userMap[userEntity.userId]?.let {
            throw CoreException(
                errorType = ErrorType.CONFLICT,
                customMessage = "이미 존재하는 사용자입니다: ${userEntity.userId}",
            )
        }
        userMap.put(userEntity.userId, userEntity)
        return userEntity
    }
}
