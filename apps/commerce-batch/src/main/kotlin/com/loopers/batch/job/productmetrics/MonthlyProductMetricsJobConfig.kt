package com.loopers.batch.job.productmetrics

import com.loopers.batch.listener.logging.LoggingStepExecutionListener
import com.loopers.batch.processor.productmetrics.MonthlyProductMetricsProcessor
import com.loopers.batch.tasklet.productrank.MonthlyProductRankTasklet
import com.loopers.batch.writer.productmetrics.MonthlyProductMetricsWriter
import com.loopers.domain.productmetrics.ProductMetricsMonthly
import com.loopers.domain.productmetrics.ProductMetricsMonthlyRepository
import com.loopers.domain.productmetrics.view.ProductMetricsView
import com.loopers.domain.productmetrics.view.ProductMetricsViewRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.support.ListItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate

@Configuration
class MonthlyProductMetricsJobConfig(
    private val jobRepository: JobRepository,
    private val txManager: PlatformTransactionManager,
    private val productMetricsMonthlyRepository: ProductMetricsMonthlyRepository,
    private val productMetricsViewRepository: ProductMetricsViewRepository,
) {

    @Bean
    fun monthlyProductMetricsJob(
        monthlyProductMetricsStep: Step,
        monthlyProductRankingStep: Step,
    ): Job =
        JobBuilder("monthlyProductMetricsJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(monthlyProductMetricsStep)
            .next(monthlyProductRankingStep)
            .build()

    @Bean
    fun monthlyProductMetricsStep(
        monthlyProductMetricsReader: ListItemReader<ProductMetricsView>,
        monthlyProductMetricsProcessor: MonthlyProductMetricsProcessor,
        monthlyProductMetricsWriter: MonthlyProductMetricsWriter,
        loggingStepExecutionListener: LoggingStepExecutionListener,
    ): Step =
        StepBuilder("monthlyProductMetricsStep", jobRepository)
            .chunk<ProductMetricsView, ProductMetricsMonthly>(CHUNK_SIZE, txManager)
            .reader(monthlyProductMetricsReader)
            .processor(monthlyProductMetricsProcessor)
            .writer(monthlyProductMetricsWriter)
            .listener(loggingStepExecutionListener)
            .build()

    @Bean
    fun monthlyProductRankingStep(
        monthlyProductRankTasklet: MonthlyProductRankTasklet,
        loggingStepExecutionListener: LoggingStepExecutionListener,
    ): Step =
        StepBuilder("monthlyProductRankingStep", jobRepository)
            .tasklet(monthlyProductRankTasklet, txManager)
            .listener(loggingStepExecutionListener)
            .build()

    @Bean
    @StepScope
    fun monthlyProductMetricsReader(
        @Value("#{jobParameters['aggregateDate']}") aggregateDateParam: String?,
    ): ListItemReader<ProductMetricsView> {
        val aggregateDate = aggregateDateParam?.let { LocalDate.parse(it) }
            ?: throw IllegalArgumentException("aggregateDate는 필수입니다.")
        val aggregateStartDate = aggregateDate.minusDays(MonthlyProductMetricsProcessor.DAYS_TO_AGGREGATE)
        val productMetricsViews = productMetricsViewRepository.findGroupedByProductInRangeOrderBySums(
            aggregateStartDate,
            aggregateDate,
            CHUNK_SIZE,
        )
        return ListItemReader(productMetricsViews)
    }

    @Bean
    fun monthlyProductMetricsWriter(): MonthlyProductMetricsWriter {
        return MonthlyProductMetricsWriter(productMetricsMonthlyRepository)
    }

    companion object {
        const val CHUNK_SIZE = 1000
    }
}
