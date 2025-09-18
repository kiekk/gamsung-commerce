package com.loopers.domain.productmetrics.view.fixture

import com.loopers.domain.productmetrics.view.ProductMetricsView
import java.time.LocalDate

class ProductMetricsViewFixture {
    private var id: Long = 0L
    private var productId: Long = 1L
    private var metricDate: LocalDate = LocalDate.now()
    private var likeCount: Int = 0
    private var viewCount: Int = 0
    private var salesCount: Int = 0

    companion object {
        fun aProductMetrics(): ProductMetricsViewFixture = ProductMetricsViewFixture()
    }

    fun id(id: Long): ProductMetricsViewFixture = apply { this.id = id }

    fun productId(productId: Long): ProductMetricsViewFixture = apply { this.productId = productId }

    fun metricDate(metricDate: LocalDate): ProductMetricsViewFixture = apply { this.metricDate = metricDate }

    fun likeCount(likeCount: Int): ProductMetricsViewFixture = apply { this.likeCount = likeCount }

    fun viewCount(viewCount: Int): ProductMetricsViewFixture = apply { this.viewCount = viewCount }

    fun salesCount(salesCount: Int): ProductMetricsViewFixture = apply { this.salesCount = salesCount }

    fun build(): ProductMetricsView {
        return ProductMetricsView(
            id,
            productId,
            metricDate,
            likeCount,
            viewCount,
            salesCount,
        )
    }
}
