package com.loopers.application.point

import com.loopers.domain.point.PointCommand
import com.loopers.domain.point.vo.Point

class PointCriteria {
    data class Charge(
        val username: String,
        val point: Point,
    ) {

        init {
            require(username.isNotBlank()) { "사용자 이름은 비어있을 수 없습니다." }
            require(point.value > 0) { "포인트 충전은 0보다 큰 값을 입력해야 합니다." }
        }

        fun toCommand(userId: Long): PointCommand.Charge {
            return PointCommand.Charge(
                userId,
                point,
            )
        }
    }
}
