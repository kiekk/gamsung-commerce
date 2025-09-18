package com.loopers.batch.job.productmetrics.processor

import com.loopers.batch.processor.productmetrics.WeeklyProductMetricsProcessor
import com.loopers.domain.productmetrics.ProductMetricsWeekly
import com.loopers.domain.productmetrics.view.fixture.ProductMetricsEntityFixture.Companion.aProductMetrics
import com.loopers.domain.productrank.ProductRankScoreCalculator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class WeeklyProductMetricsProcessorTest {
    @Test
    @DisplayName("주간 프로세서는 뷰를 주간 엔티티로 변환하며 기간·버전·스코어를 올바르게 설정한다")
    fun shouldMapViewToWeeklyEntityWithCorrectWindowVersionAndScore() {
        // arrange
        val aggregateDate = LocalDate.of(2025, 9, 19)
        val version = "v1"
        val processor = WeeklyProductMetricsProcessor(
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
        val expectedStart = aggregateDate.minusDays(WeeklyProductMetricsProcessor.DAYS_TO_AGGREGATE)
        val expectedEndExclusiveMinus1 = aggregateDate.minusDays(1)

        // act
        val productMetricsWeekly: ProductMetricsWeekly = processor.process(productMetricsView)

        // assert
        assertAll(
            { assertThat(productMetricsWeekly).isNotNull },
            { assertThat(productMetricsWeekly.productId).isEqualTo(1L) },
            { assertThat(productMetricsWeekly.version).isEqualTo(version) },
            { assertThat(productMetricsWeekly.aggregateStartDate).isEqualTo(expectedStart) },
            { assertThat(productMetricsWeekly.aggregateEndDate).isEqualTo(expectedEndExclusiveMinus1) },
            { assertThat(productMetricsWeekly.aggregateDate).isEqualTo(aggregateDate) },
            { assertThat(productMetricsWeekly.likeCount).isEqualTo(10) },
            { assertThat(productMetricsWeekly.viewCount).isEqualTo(20) },
            { assertThat(productMetricsWeekly.salesCount).isEqualTo(30) },
        )

        val expectedScore = ProductRankScoreCalculator.calculate(10, 20, 30)
        assertThat(productMetricsWeekly.score).isEqualTo(expectedScore)
    }

    @Test
    @DisplayName("aggregateDate 파라미터가 없으면 예외가 발생한다.")
    fun shouldThrowExceptionWhenAggregateDateIsMissing() {
        // arrange
        val version = "v1"

        // act
        val exception = assertThrows<IllegalArgumentException> {
            WeeklyProductMetricsProcessor(
                null,
                version,
            )
        }

        // assert
        assertThat(exception.message).isEqualTo("aggregateDate는 필수입니다.")
    }

    @Test
    @DisplayName("version 파라미터가 없으면 예외가 발생한다.")
    fun shouldThrowExceptionWhenVersionIsMissing() {
        // arrange
        val aggregateDate = LocalDate.of(2025, 9, 19)

        // act
        val exception = assertThrows<IllegalArgumentException> {
            WeeklyProductMetricsProcessor(
                aggregateDate.toString(),
                null,
            )
        }

        // assert
        assertThat(exception.message).isEqualTo("version는 필수입니다.")
    }
}
