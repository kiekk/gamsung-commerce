package com.loopers.domain.productrank

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "product_rank_hourly")
class ProductRankHourly(
    val productId: String,
    val rankDateTime: String,
    val rankNumber: Int,
    val score: Double,
) : BaseEntity()
