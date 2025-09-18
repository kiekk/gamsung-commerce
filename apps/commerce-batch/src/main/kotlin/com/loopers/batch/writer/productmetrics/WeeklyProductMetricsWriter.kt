package com.loopers.batch.writer.productmetrics

import com.loopers.domain.productmetrics.ProductMetricsWeekly
import com.loopers.domain.productmetrics.ProductMetricsWeeklyRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter

class WeeklyProductMetricsWriter(
    private val productMetricsWeeklyRepository: ProductMetricsWeeklyRepository,
) : ItemWriter<ProductMetricsWeekly> {

    override fun write(chunk: Chunk<out ProductMetricsWeekly?>) {
        productMetricsWeeklyRepository.saveAll(chunk.items)
    }
}
