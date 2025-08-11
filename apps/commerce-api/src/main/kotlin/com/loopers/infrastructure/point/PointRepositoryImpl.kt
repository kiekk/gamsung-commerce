package com.loopers.infrastructure.point

import com.loopers.domain.point.PointEntity
import com.loopers.domain.point.PointRepository
import org.springframework.stereotype.Repository

@Repository
class PointRepositoryImpl(
    private val pointJpaRepository: PointJpaRepository,
) : PointRepository {
    override fun save(pointEntity: PointEntity): PointEntity {
        return pointJpaRepository.save(pointEntity)
    }

    override fun findByUserId(userId: Long): PointEntity? {
        return pointJpaRepository.findByUserId(userId)
    }

    override fun findByUserIdWithLock(userId: Long): PointEntity? {
        return pointJpaRepository.findByUserIdWithLock(userId)
    }
}
