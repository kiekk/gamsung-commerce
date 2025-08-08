package com.loopers.application.point

import com.loopers.domain.point.PointEntity

class PointInfo {
    data class PointDetail(
        val userId: Long,
        val point: Long,
    ) {
        companion object {
            fun from(pointEntity: PointEntity): PointDetail {
                return PointDetail(
                    pointEntity.userId,
                    pointEntity.point.value,
                )
            }
        }
    }

    data class PointResult(
        val userId: Long,
        val point: Long,
    ) {
        companion object {
            fun from(pointEntity: PointEntity): PointResult {
                return PointResult(
                    pointEntity.userId,
                    pointEntity.point.value,
                )
            }
        }
    }
}
