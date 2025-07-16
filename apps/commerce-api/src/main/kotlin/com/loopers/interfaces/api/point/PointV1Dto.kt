package com.loopers.interfaces.api.point

class PointV1Dto {

    data class PointResponse(
        val userId: String,
        val point: Long,
    ) {
        companion object {
            fun from(userId: String, point: Long): PointResponse = PointResponse(
                userId = userId,
                point = point,
            )
        }
    }
}
