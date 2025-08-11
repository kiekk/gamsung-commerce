package com.loopers.domain.point

import com.loopers.domain.BaseEntity
import com.loopers.domain.point.vo.Point
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(name = "point")
class PointEntity(
    @Column(name = "user_id", nullable = false, unique = true)
    val userId: Long,
    point: Point = Point.ZERO,
) : BaseEntity() {
    var point: Point = point
        private set

    @Version
    var version: Long? = null

    fun chargePoint(point: Point) {
        if (point.value <= 0) {
            throw CoreException(ErrorType.BAD_REQUEST, "충전할 포인트는 1 이상이어야 합니다.")
        }
        this.point = Point(this.point.value + point.value)
    }

    fun usePoint(pointToUser: Point) {
        this.point = Point(this.point.value - pointToUser.value)
    }

    fun cannotUsePoint(pointToUser: Point): Boolean {
        return pointToUser.value > point.value
    }

    fun refundPoint(point: Point) {
        this.point = Point(this.point.value + point.value)
    }
}
