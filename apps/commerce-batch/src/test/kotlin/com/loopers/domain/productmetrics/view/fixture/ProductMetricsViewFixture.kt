package com.loopers.domain.productmetrics.view.fixture

import com.loopers.domain.productmetrics.view.ProductMetricsView
import java.time.LocalDate

class ProductMetricsEntityFixture {
    private var id: Long = 0L
    private var productId: Long = 1L
    private var metricDate: LocalDate = LocalDate.now()
    private var likeCount: Int = 0
    private var viewCount: Int = 0
    private var salesCount: Int = 0

    companion object {
        fun aProductMetrics(): ProductMetricsEntityFixture = ProductMetricsEntityFixture()
    }

    fun id(id: Long): ProductMetricsEntityFixture = apply { this.id = id }

    fun productId(productId: Long): ProductMetricsEntityFixture = apply { this.productId = productId }

    fun metricDate(metricDate: LocalDate): ProductMetricsEntityFixture = apply { this.metricDate = metricDate }

    fun likeCount(likeCount: Int): ProductMetricsEntityFixture = apply { this.likeCount = likeCount }

    fun viewCount(viewCount: Int): ProductMetricsEntityFixture = apply { this.viewCount = viewCount }

    fun salesCount(salesCount: Int): ProductMetricsEntityFixture = apply { this.salesCount = salesCount }

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
