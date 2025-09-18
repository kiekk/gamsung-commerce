package com.loopers.batch.job.productmetrics.reader

import com.loopers.batch.job.productmetrics.MonthlyProductMetricsJobConfig
import com.loopers.domain.productmetrics.ProductMetricsMonthlyRepository
import com.loopers.domain.productmetrics.view.ProductMetricsView
import com.loopers.domain.productmetrics.view.ProductMetricsViewRepository
import com.loopers.domain.productmetrics.view.fixture.ProductMetricsViewFixture.Companion.aProductMetrics
import com.loopers.infrastructure.productmetrics.view.ProductMetricsViewJpaRepository
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.item.support.ListItemReader
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate

@SpringBootTest
class MonthlyProductMetricsReaderTest @Autowired constructor(
    private val productMetricsViewRepository: ProductMetricsViewRepository,
    private val productMetricsViewJpaRepository: ProductMetricsViewJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun cleanUp() {
        databaseCleanUp.truncateAllTables()
    }

    @Test
    @DisplayName("기간 내 product_id별 합계로 집계하고 'SUM(sales)→SUM(view)→SUM(like)' 내림차순으로 반환한다.")
    fun shouldCreateReaderAndReadSequentially() {
        // arrange
        val jobRepository = mock<JobRepository>()
        val txManager = mock<PlatformTransactionManager>()
        val monthlyRepo = mock<ProductMetricsMonthlyRepository>()

        val aggregateDate = LocalDate.of(2025, 9, 19)

        val productMetricsView1 = aProductMetrics()
            .id(1)
            .productId(1)
            .metricDate(aggregateDate.minusDays(1))
            .likeCount(10)
            .viewCount(20)
            .salesCount(30)
            .build()
        val productMetricsView2 = aProductMetrics()
            .id(2)
            .productId(2)
            .metricDate(aggregateDate.minusDays(1))
            .likeCount(10)
            .viewCount(20)
            .salesCount(35)
            .build()
        productMetricsViewJpaRepository.saveAll(listOf(productMetricsView1, productMetricsView2))

        val config = MonthlyProductMetricsJobConfig(jobRepository, txManager, monthlyRepo, productMetricsViewRepository)

        // act
        val reader: ListItemReader<ProductMetricsView> =
            config.monthlyProductMetricsReader(aggregateDate.toString())

        // assert
        assertAll(
            { assertThat(reader.read()!!.productId).isEqualTo(productMetricsView2.productId) },
            { assertThat(reader.read()!!.productId).isEqualTo(productMetricsView1.productId) },
            { assertThat(reader.read()).isNull() },
        )
    }

    @Test
    @DisplayName("aggregateDate 파라미터가 없으면 예외가 발생한다.")
    fun shouldThrowExceptionWhenAggregateDateIsNull() {
        // arrange
        val jobRepository = mock<JobRepository>()
        val txManager = mock<PlatformTransactionManager>()
        val monthlyRepo = mock<ProductMetricsMonthlyRepository>()
        val config = MonthlyProductMetricsJobConfig(jobRepository, txManager, monthlyRepo, productMetricsViewRepository)

        // act
        val exception = assertThrows<IllegalArgumentException> {
            config.monthlyProductMetricsReader(null)
        }

        // assert
        assertThat(exception.message).isEqualTo("aggregateDate는 필수입니다.")
    }
}
