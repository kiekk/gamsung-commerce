package com.loopers.application.point

import com.loopers.domain.point.PointService
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PointFacade(
    private val pointService: PointService,
    private val userService: UserService,
) {
    @Transactional(readOnly = true)
    fun getPoint(username: String): PointInfo.PointDetail? {
        val user = userService.findUserBy(username) ?: return null
        return pointService.getPointBy(user.id)
            ?.let { pointEntity -> PointInfo.PointDetail(pointEntity.userId, pointEntity.point.value) }
    }

    @Transactional
    fun chargePoint(criteria: PointCriteria.Charge): PointInfo.PointResult {
        val user = userService.findUserBy(criteria.username) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "사용자를 찾을 수 없습니다: ${criteria.username}",
        )
        return pointService.chargePoint(criteria.toCommand(user.id)).let {
            PointInfo.PointResult(it.userId, it.point.value)
        }
    }
}
