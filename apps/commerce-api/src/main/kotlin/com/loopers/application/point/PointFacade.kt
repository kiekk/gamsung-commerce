package com.loopers.application.point

import com.loopers.domain.point.PointService
import org.springframework.stereotype.Component

@Component
class PointFacade(
    private val pointService: PointService,
) {
    fun getUserPoints(userId: String): PointInfo? {
        return pointService.getPoints(userId)
            ?.let { pointEntity -> PointInfo(userId = pointEntity.userId, point = pointEntity.point) }
    }
}
