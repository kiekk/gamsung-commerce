package com.loopers.domain.point

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PointService(
    private val pointRepository: PointRepository,
) {
    @Transactional
    fun chargePoint(userId: String, point: Long): PointEntity {
        val pointEntity = pointRepository.findByUserId(userId) ?: PointEntity(userId, 0L)
        pointEntity.chargePoint(point)
        return pointRepository.save(pointEntity)
    }

    @Transactional(readOnly = true)
    fun getPoint(userId: String): PointEntity? {
        return pointRepository.findByUserId(userId)
    }
}
