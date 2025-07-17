package com.loopers.application.point

import org.springframework.stereotype.Component

@Component
class PointFacade(
) {
    fun getUserPoints(userId: String): PointInfo {
        return PointInfo(
            userId = userId,
            point = 1000L,
        )
    }
}
