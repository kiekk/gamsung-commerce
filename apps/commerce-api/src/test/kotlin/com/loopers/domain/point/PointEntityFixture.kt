package com.loopers.domain.point

import com.loopers.domain.point.vo.Point

class PointEntityFixture {
    private var userId: Long = 1L
    private var point: Point = Point(100L)

    companion object {
        fun aPoint(): PointEntityFixture = PointEntityFixture()
    }

    fun userId(userId: Long): PointEntityFixture = apply { this.userId = userId }

    fun point(point: Point): PointEntityFixture = apply { this.point = point }

    fun build(): PointEntity = PointEntity(
        userId,
        point,
    )
}
