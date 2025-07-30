package com.loopers.domain.point

class PointCommand {
    data class Charge(
        val userId: String,
        val point: Long,
    ) {
        init {
            require(point > 0) { "포인트 충전은 0보다 큰 값을 입력해야 합니다." }
        }

        fun toEntity(): PointEntity {
            return PointEntity(userId, point)
        }
    }
}
