package com.loopers.domain.productlike.view

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "product_like_count")
class ProductLikeCountView(
    @Id
    val productId: Long,
    val productLikeCount: Int,
)
