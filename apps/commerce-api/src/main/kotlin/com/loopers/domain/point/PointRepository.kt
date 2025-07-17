package com.loopers.domain.point

interface PointRepository {
    fun save(pointEntity: PointEntity): PointEntity

    fun getPoint(userId: String): PointEntity?
}
