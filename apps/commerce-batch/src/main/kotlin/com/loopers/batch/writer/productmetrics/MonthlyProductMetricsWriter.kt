package com.loopers.batch.writer.productmetrics

import com.loopers.domain.productmetrics.ProductMetricsMonthly
import com.loopers.domain.productmetrics.ProductMetricsMonthlyRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter

class MonthlyProductMetricsWriter(
    private val productMetricsMonthlyRepository: ProductMetricsMonthlyRepository,
) : ItemWriter<ProductMetricsMonthly> {

    override fun write(chunk: Chunk<out ProductMetricsMonthly?>) {
        productMetricsMonthlyRepository.saveAll(chunk.items)
    }
}
