package com.loopers.interfaces.api.point

import com.loopers.application.point.PointCriteria
import com.loopers.application.point.PointInfo
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

    data class PointResultResponse(
        val userId: Long,
        val point: Long,
    ) {
        companion object {
            fun from(pointResult: PointInfo.PointResult): PointResultResponse = PointResultResponse(
                pointResult.userId,
                pointResult.point,
            )
        }
    }

    data class PointDetailResponse(
        val userId: Long,
        val point: Long,
    ) {
        companion object {
            fun from(pointDetail: PointInfo.PointDetail): PointResultResponse = PointResultResponse(
                pointDetail.userId,
                pointDetail.point,
            )
        }
    }
}
