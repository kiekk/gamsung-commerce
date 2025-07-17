package com.loopers.domain.point

import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointRepository: PointRepository,
) {
    fun getPoints(userId: String): PointEntity? {
        return pointRepository.getPoints(userId)
    }
}
