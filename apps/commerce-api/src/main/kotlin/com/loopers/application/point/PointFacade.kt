package com.loopers.application.point

import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component

@Component
class PointFacade(
    private val userService: UserService,
) {
    fun getUserPoints(userId: String): PointInfo? {
        // user가 있으면 PointInfo 반환, 없으면 null 반환
        return userService.getUser(userId)?.let {
            PointInfo(
                userId = userId,
                point = 0L,
            )
        }
    }
}
