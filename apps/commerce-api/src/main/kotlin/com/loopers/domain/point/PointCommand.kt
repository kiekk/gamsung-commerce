package com.loopers.domain.point

import com.loopers.domain.point.vo.Point

class PointCommand {
    data class Charge(
        val userId: Long,
        val point: Point,
    ) {
        init {
            require(userId > 0) { "사용자 ID는 0보다 커야 합니다." }
            require(point.value > 0) { "포인트 충전은 0보다 큰 값을 입력해야 합니다." }
        }

        fun toEntity(): PointEntity {
            return PointEntity(userId, point)
        }
    }
}
