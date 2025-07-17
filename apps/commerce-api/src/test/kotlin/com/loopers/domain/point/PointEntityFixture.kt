package com.loopers.domain.point

class PointEntityFixture {
    private var userId: String = "userId123"
    private var point: Long = 100L

    companion object {
        fun aPoint(): PointEntityFixture = PointEntityFixture()
    }

    fun userId(userId: String): PointEntityFixture = apply { this.userId = userId }

    fun point(point: Long): PointEntityFixture = apply { this.point = point }

    fun build(): PointEntity = PointEntity(
        userId = userId,
        point = point,
    )
}
