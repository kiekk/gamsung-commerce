package com.loopers.infrastructure.point

import com.loopers.domain.point.PointEntity
import com.loopers.domain.point.PointRepository
import org.springframework.stereotype.Repository

@Repository
class PointRepositoryImpl(
    private val pointJpaRepository: PointJpaRepository,
) : PointRepository {
    override fun getPoints(userId: String): PointEntity? {
        return pointJpaRepository.findByUserId(userId)
    }
}
