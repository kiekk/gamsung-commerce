package com.loopers.interfaces.api.point

import jakarta.validation.constraints.Min

class PointV1Dto {

    data class ChargeRequest(
        @Min(value = 1, message = "포인트는 1 이상이어야 합니다.")
        val point: Long,
    ) {
    }

    data class PointResponse(
        val userId: String,
        val point: Long,
    ) {
        companion object {
            fun from(userId: String, point: Long): PointResponse = PointResponse(
                userId,
                point,
            )
        }
    }
}
