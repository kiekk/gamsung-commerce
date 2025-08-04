package com.loopers.infrastructure.user

import com.loopers.domain.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(userId: String): UserEntity?
}
