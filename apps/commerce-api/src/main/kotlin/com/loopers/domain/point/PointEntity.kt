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
    @Column(name = "point", nullable = false)
    val point: Long,
) : BaseEntity() {

    init {
        if (point <= 0) {
            throw CoreException(ErrorType.BAD_REQUEST, "포인트는 1 이상이어야 합니다.")
        }
    }
}
