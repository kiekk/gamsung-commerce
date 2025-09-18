package com.loopers.domain.productmetrics.view

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "product_metrics")
class ProductMetricsView(
    @Id
    val id: Long = 0,
    val productId: Long,
    val metricDate: LocalDate,
    val likeCount: Int,
    val viewCount: Int,
    val salesCount: Int,
)
