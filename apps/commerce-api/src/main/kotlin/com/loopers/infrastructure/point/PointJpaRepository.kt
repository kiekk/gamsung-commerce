package com.loopers.infrastructure.point

import com.loopers.domain.point.PointEntity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface PointJpaRepository : JpaRepository<PointEntity, Long> {
    fun findByUserId(userId: Long): PointEntity?

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM PointEntity p WHERE p.userId = :userId")
    fun findByUserIdWithLock(userId: Long): PointEntity?
}
