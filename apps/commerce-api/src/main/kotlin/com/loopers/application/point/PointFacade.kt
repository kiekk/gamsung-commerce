package com.loopers.application.point

import com.loopers.domain.point.PointService
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class PointFacade(
    private val pointService: PointService,
    private val userService: UserService,
) {
    fun getUserPoint(userId: String): PointInfo? {
        return pointService.getPoint(userId)
            ?.let { pointEntity -> PointInfo(userId = pointEntity.userId, point = pointEntity.point) }
    }

    fun chargePoint(userId: String, point: Long): PointInfo? {
        userService.getUser(userId) ?: throw CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다: $userId")
        return pointService.chargePoint(userId, point).let {
            PointInfo(userId = it.userId, point = it.point)
        }
    }
}
