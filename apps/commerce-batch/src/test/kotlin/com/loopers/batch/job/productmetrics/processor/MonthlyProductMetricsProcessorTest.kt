package com.loopers.batch.job.productmetrics.processor

import com.loopers.batch.processor.productmetrics.MonthlyProductMetricsProcessor
import com.loopers.domain.productmetrics.ProductMetricsMonthly
import com.loopers.domain.productmetrics.view.fixture.ProductMetricsEntityFixture.Companion.aProductMetrics
import com.loopers.domain.productrank.ProductRankScoreCalculator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.time.LocalDate

class MonthlyProductMetricsProcessorTest {
    @Test
    @DisplayName("월간 프로세서는 뷰를 월간 엔티티로 변환하며 기간·버전·스코어를 올바르게 설정한다")
    fun shouldMapViewToMonthlyEntityWithCorrectWindowVersionAndScore() {
        // arrange
        val aggregateDate = LocalDate.of(2025, 9, 19)
        val version = "v1"
        val processor = MonthlyProductMetricsProcessor(
            aggregateDate.toString(),
            version,
        )
        val productMetricsView = aProductMetrics()
            .id(1)
            .productId(1)
            .metricDate(aggregateDate.minusDays(1))
            .likeCount(10)
            .viewCount(20)
            .salesCount(30)
            .build()
        val expectedStart = aggregateDate.minusDays(MonthlyProductMetricsProcessor.DAYS_TO_AGGREGATE)
        val expectedEndExclusiveMinus1 = aggregateDate.minusDays(1)

        // act
        val productMetricsMonthly: ProductMetricsMonthly = processor.process(productMetricsView)

        // assert
        assertAll(
            { assertThat(productMetricsMonthly).isNotNull },
            { assertThat(productMetricsMonthly.productId).isEqualTo(1L) },
            { assertThat(productMetricsMonthly.version).isEqualTo(version) },
            { assertThat(productMetricsMonthly.aggregateStartDate).isEqualTo(expectedStart) },
            { assertThat(productMetricsMonthly.aggregateEndDate).isEqualTo(expectedEndExclusiveMinus1) },
            { assertThat(productMetricsMonthly.aggregateDate).isEqualTo(aggregateDate) },
            { assertThat(productMetricsMonthly.likeCount).isEqualTo(10) },
            { assertThat(productMetricsMonthly.viewCount).isEqualTo(20) },
            { assertThat(productMetricsMonthly.salesCount).isEqualTo(30) },
        )

        val expectedScore = ProductRankScoreCalculator.calculate(10, 20, 30)
        assertThat(productMetricsMonthly.score).isEqualTo(expectedScore)
    }

    @Test
    @DisplayName("aggregateDate 파라미터가 없으면 예외가 발생한다.")
    fun shouldThrowExceptionWhenAggregateDateIsMissing() {
        // arrange
        val version = "v1"

        // act & assert
        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            MonthlyProductMetricsProcessor(
                null,
                version,
            )
        }
        assertThat(exception.message).isEqualTo("aggregateDate는 필수입니다.")
    }

    @Test
    @DisplayName("version 파라미터가 없으면 예외가 발생한다.")
    fun shouldThrowExceptionWhenVersionIsMissing() {
        // arrange
        val aggregateDate = LocalDate.of(2025, 9, 19)

        // act
        val exception = org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            MonthlyProductMetricsProcessor(
                aggregateDate.toString(),
                null,
            )
        }

        // assert
        assertThat(exception.message).isEqualTo("version는 필수입니다.")
    }
}
