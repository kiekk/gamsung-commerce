package com.loopers.domain.point

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "point")
class PointEntity(
    @Column(name = "user_id", nullable = false, unique = true)
    val userId: String,
    point: Long = 0L,
) : BaseEntity() {
    var point: Long = point
        private set

    fun chargePoint(point: Long) {
        if (point <= 0) {
            throw CoreException(ErrorType.BAD_REQUEST, "충전할 포인트는 1 이상이어야 합니다.")
        }
        this.point += point
    }
}
