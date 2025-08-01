package com.loopers.application.point

import com.loopers.domain.point.PointCommand
import com.loopers.domain.point.vo.Point

class PointCriteria {
    data class Charge(
        val userId: String,
        val point: Point,
    ) {

        init {
            require(point.value > 0) { "포인트 충전은 0보다 큰 값을 입력해야 합니다." }
        }

        fun toCommand(): PointCommand.Charge {
            return PointCommand.Charge(
                userId,
                point,
            )
        }
    }
}
