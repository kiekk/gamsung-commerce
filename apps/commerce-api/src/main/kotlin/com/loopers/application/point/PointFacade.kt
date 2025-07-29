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
    fun getPoint(userId: String): PointInfo? {
        return pointService.getPoint(userId)
            ?.let { pointEntity -> PointInfo(pointEntity.userId, pointEntity.point) }
    }

    fun chargePoint(criteria: PointCriteria.Charge): PointInfo? {
        userService.findUserBy(criteria.userId) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "사용자를 찾을 수 없습니다: ${criteria.userId}",
        )
        return pointService.chargePoint(criteria.toCommand()).let {
            PointInfo(it.userId, it.point)
        }
    }
}
