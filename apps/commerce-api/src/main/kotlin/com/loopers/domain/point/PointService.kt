package com.loopers.domain.point

import com.loopers.domain.point.vo.Point
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PointService(
    private val pointRepository: PointRepository,
) {
    @Transactional
    fun chargePoint(command: PointCommand.Charge): PointEntity {
        val pointEntity = pointRepository.findByUserId(command.userId) ?: PointEntity(command.userId, Point.ZERO)
        pointEntity.chargePoint(command.point)
        return pointRepository.save(pointEntity)
    }

    @Transactional(readOnly = true)
    fun getPoint(userId: String): PointEntity? {
        return pointRepository.findByUserId(userId)
    }
}
