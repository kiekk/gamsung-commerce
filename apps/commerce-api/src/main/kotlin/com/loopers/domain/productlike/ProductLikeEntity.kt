package com.loopers.domain.productlike

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "product_like",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "product_id"]),
    ],
)
class ProductLikeEntity(
    val userId: Long,
    val productId: Long,
) : BaseEntity() {
}
