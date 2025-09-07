package com.loopers.domain.productmetrics

import com.loopers.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDate

@Entity
@Table(
    name = "product_metrics",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_product_metrics_product_date",
            columnNames = ["product_id", "metric_date"],
        ),
    ],
    indexes = [
        Index(name = "idx_product_metrics_product_id", columnList = "product_id"),
        Index(name = "idx_product_metrics_metric_date", columnList = "metric_date"),
    ],
)
class ProductMetrics(
    val productId: Long,
    val metricDate: LocalDate,
    var likeCount: Int = 0,
    var viewCount: Int = 0,
    var salesCount: Int = 0,
) : BaseEntity() {

    init {
        require(productId > 0) { "상품 ID는 0보다 커야 합니다." }
        require(likeCount >= 0) { "상품 좋아요 수는 0보다 작을 수 없습니다." }
        require(viewCount >= 0) { "상품 조회 수는 0보다 작을 수 없습니다." }
        require(salesCount >= 0) { "상품 판매 수는 0보다 작을 수 없습니다." }
    }

    fun increaseSalesCount(count: Int = 1) {
        this.salesCount += count
    }

    fun increaseViewCount(count: Int = 1) {
        this.viewCount += count
    }

    fun increaseLikeCount(count: Int = 1) {
        this.likeCount += count
    }

    fun decreaseLikeCount(count: Int = 1) {
        this.likeCount -= count
    }

    companion object {
        fun init(productId: Long, metricDate: LocalDate): ProductMetrics {
            return ProductMetrics(
                productId,
                metricDate,
            )
        }
    }
}
