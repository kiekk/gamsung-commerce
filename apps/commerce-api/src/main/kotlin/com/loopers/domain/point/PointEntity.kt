package com.loopers.domain.point

import com.loopers.domain.BaseEntity
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
}
