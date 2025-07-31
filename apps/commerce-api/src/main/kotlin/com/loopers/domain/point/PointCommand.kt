package com.loopers.domain.point

import com.loopers.domain.point.vo.Point

class PointCommand {
    data class Charge(
        val userId: String,
        val point: Point,
    ) {
        init {
            require(point.value > 0) { "포인트 충전은 0보다 큰 값을 입력해야 합니다." }
        }

        fun toEntity(): PointEntity {
            return PointEntity(userId, point)
        }
    }
}
