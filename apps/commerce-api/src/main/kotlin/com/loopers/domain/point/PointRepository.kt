package com.loopers.domain.point

interface PointRepository {
    fun getPoints(userId: String): PointEntity?
}
