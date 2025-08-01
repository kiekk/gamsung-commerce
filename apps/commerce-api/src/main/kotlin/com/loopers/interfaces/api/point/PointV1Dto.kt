package com.loopers.interfaces.api.point

import com.loopers.application.point.PointCriteria
import com.loopers.domain.point.vo.Point
import jakarta.validation.constraints.Min

class PointV1Dto {

    data class ChargeRequest(
        @field:Min(value = 1, message = "포인트는 1 이상이어야 합니다.")
        val point: Long,
    ) {
        fun toCriteria(username: String): PointCriteria.Charge {
            return PointCriteria.Charge(
                username,
                Point(point),
            )
        }
    }

    data class PointResponse(
        val userId: Long,
        val point: Long,
    ) {
        companion object {
            fun from(userId: Long, point: Long): PointResponse = PointResponse(
                userId,
                point,
            )
        }
    }
}
