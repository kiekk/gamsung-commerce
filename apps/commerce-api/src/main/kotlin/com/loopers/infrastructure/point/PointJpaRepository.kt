package com.loopers.infrastructure.point

import com.loopers.domain.point.PointEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PointJpaRepository : JpaRepository<PointEntity, Long> {
    fun findByUserId(userId: Long): PointEntity?
}
