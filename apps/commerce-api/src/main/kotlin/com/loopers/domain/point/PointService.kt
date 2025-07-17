package com.loopers.domain.point

import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointRepository: PointRepository,
) {
    fun chargePoint(userId: String, point: Long): PointEntity {
        val pointEntity = pointRepository.getPoint(userId) ?: PointEntity(userId, 0L)
        pointEntity.chargePoint(point)
        return pointRepository.save(pointEntity)
    }

    fun getPoint(userId: String): PointEntity? {
        return pointRepository.getPoint(userId)
    }
}
