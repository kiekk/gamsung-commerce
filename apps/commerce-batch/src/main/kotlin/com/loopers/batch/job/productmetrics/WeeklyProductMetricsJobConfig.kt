package com.loopers.batch.job.productmetrics

import com.loopers.batch.listener.logging.LoggingStepExecutionListener
import com.loopers.batch.processor.productmetrics.WeeklyProductMetricsProcessor
import com.loopers.batch.tasklet.productrank.WeeklyProductRankTasklet
import com.loopers.batch.writer.productmetrics.WeeklyProductMetricsWriter
import com.loopers.domain.productmetrics.ProductMetricsWeekly
import com.loopers.domain.productmetrics.ProductMetricsWeeklyRepository
import com.loopers.domain.productmetrics.view.ProductMetricsView
import com.loopers.domain.productmetrics.view.ProductMetricsViewRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.support.ListItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate

@Configuration
class WeeklyProductMetricsJobConfig(
    private val jobRepository: JobRepository,
    private val txManager: PlatformTransactionManager,
    private val productMetricsWeeklyRepository: ProductMetricsWeeklyRepository,
    private val productMetricsViewRepository: ProductMetricsViewRepository,
) {

    @Bean
    fun weeklyProductMetricsJob(
        weeklyProductMetricsStep: Step,
        weeklyProductRankingStep: Step,
    ): Job =
        JobBuilder("weeklyProductMetricsJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(weeklyProductMetricsStep)
            .next(weeklyProductRankingStep)
            .build()

    @Bean
    fun weeklyProductMetricsStep(
        weeklyProductMetricsReader: ItemReader<ProductMetricsView>,
        weeklyProductMetricsProcessor: WeeklyProductMetricsProcessor,
        weeklyProductMetricsWriter: WeeklyProductMetricsWriter,
        loggingStepExecutionListener: LoggingStepExecutionListener,
    ): Step =
        StepBuilder("weeklyProductMetricsStep", jobRepository)
            .chunk<ProductMetricsView, ProductMetricsWeekly>(CHUNK_SIZE, txManager)
            .reader(weeklyProductMetricsReader)
            .processor(weeklyProductMetricsProcessor)
            .writer(weeklyProductMetricsWriter)
            .listener(loggingStepExecutionListener)
            .build()

    @Bean
    fun weeklyProductRankingStep(
        weeklyProductRankTasklet: WeeklyProductRankTasklet,
        loggingStepExecutionListener: LoggingStepExecutionListener,
    ): Step =
        StepBuilder("weeklyProductRankingStep", jobRepository)
            .tasklet(weeklyProductRankTasklet, txManager)
            .listener(loggingStepExecutionListener)
            .build()

    @Bean
    @StepScope
    fun weeklyProductMetricsReader(
        @Value("#{jobParameters['aggregateDate']}") aggregateDateParam: String?,
    ): ListItemReader<ProductMetricsView> {
        val aggregateDate = aggregateDateParam?.let { LocalDate.parse(it) }
            ?: throw IllegalArgumentException("aggregateDate는 필수입니다.")
        val aggregateStartDate = aggregateDate.minusDays(WeeklyProductMetricsProcessor.DAYS_TO_AGGREGATE)
        val productMetricsViews = productMetricsViewRepository.findGroupedByProductInRangeOrderBySums(
            aggregateStartDate,
            aggregateDate,
            CHUNK_SIZE,
        )
        return ListItemReader(productMetricsViews)
    }

    @Bean
    fun weeklyProductMetricsWriter(): WeeklyProductMetricsWriter {
        return WeeklyProductMetricsWriter(productMetricsWeeklyRepository)
    }

    companion object {
        const val CHUNK_SIZE = 1000
    }
}
