package com.loopers.batch.processor.productmetrics

import com.loopers.domain.productmetrics.ProductMetricsWeekly
import com.loopers.domain.productmetrics.view.ProductMetricsView
import com.loopers.domain.productrank.ProductRankScoreCalculator
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@StepScope
@Component
class WeeklyProductMetricsProcessor(
    @Value("#{jobParameters['aggregateDate']}") private val aggregateDateParam: String?,
    @Value("#{jobParameters['version']}") private val versionParam: String?,
) : ItemProcessor<ProductMetricsView, ProductMetricsWeekly> {

    private val aggregateDate = aggregateDateParam?.let { LocalDate.parse(it) }
        ?: throw IllegalArgumentException("aggregateDate는 필수입니다.")
    private val version = versionParam ?: throw IllegalArgumentException("version는 필수입니다.")

    override fun process(item: ProductMetricsView): ProductMetricsWeekly {
        val score = ProductRankScoreCalculator.calculate(item.likeCount, item.viewCount, item.salesCount)

        return ProductMetricsWeekly(
            item.productId,
            version,
            aggregateDate.minusDays(DAYS_TO_AGGREGATE),
            aggregateDate.minusDays(1),
            aggregateDate,
            score,
            item.likeCount,
            item.viewCount,
            item.salesCount,
            LocalDateTime.now(),
        )
    }

    companion object {
        const val DAYS_TO_AGGREGATE = 7L
    }
}
