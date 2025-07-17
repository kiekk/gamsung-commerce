package com.loopers.application.point

import com.loopers.domain.point.PointService
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component

@Component
class PointFacade(
    private val userService: UserService,
    private val pointService: PointService,
) {
    fun getUserPoints(userId: String): PointInfo? {
        // user가 있으면 PointInfo 반환, 없으면 null 반환
        return userService.getUser(userId)?.let {
            pointService.getPoints(it.userId)
                ?.let { points -> PointInfo(userId = it.userId, point = points) }
        }
    }
}
