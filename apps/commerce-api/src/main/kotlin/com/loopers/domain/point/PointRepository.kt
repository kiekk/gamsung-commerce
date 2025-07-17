package com.loopers.domain.point

interface PointRepository {
    fun save(pointEntity: PointEntity): PointEntity

    fun findByUserId(userId: String): PointEntity?
}
