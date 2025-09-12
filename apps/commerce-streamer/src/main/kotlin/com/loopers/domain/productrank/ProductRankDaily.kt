package com.loopers.domain.productrank

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "product_rank_daily")
class ProductRankDaily(
    val productId: String,
    val rankDate: String,
    val rankNumber: Int,
    val score: Double,
) : BaseEntity()
