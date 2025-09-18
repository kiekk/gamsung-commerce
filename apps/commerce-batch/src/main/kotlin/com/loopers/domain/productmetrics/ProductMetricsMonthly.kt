package com.loopers.domain.productmetrics

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "product_metrics_monthly")
class ProductMetricsMonthly(
    @Id
    val productId: Long,
    val version: String,
    val aggregateStartDate: LocalDate,
    val aggregateEndDate: LocalDate,
    val aggregateDate: LocalDate,
    val score: Double,
    val likeCount: Int,
    val viewCount: Int,
    val salesCount: Int,
    val updatedAt: LocalDateTime,
)
