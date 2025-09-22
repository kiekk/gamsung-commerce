package com.loopers.interfaces.batch.scheduler.productmetrics

import com.loopers.support.enums.batch.BatchType
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDate

@Configuration
@EnableScheduling
class ProductMetricsV1BatchScheduler(
    private val jobLauncher: JobLauncher,
    @Qualifier("weeklyProductMetricsJob") private val weeklyProductMetricsJob: Job,
    @Qualifier("monthlyProductMetricsJob") private val monthlyProductMetricsJob: Job,
) {
    /**
     * 매일 00:05 (KST) 실행 → 주간 집계: [aggregateDate-7, aggregateDate) 구간
     */
    @Scheduled(cron = "0 5 0 * * ?")
    fun productMetricsWeeklyJob() {
        val aggregateDate = LocalDate.now()
        val params = JobParametersBuilder()
            .addString("aggregateDate", aggregateDate.toString())
            .addString("version", "v1")
            .addString("batchType", BatchType.WEEKLY.name)
            .toJobParameters()
        jobLauncher.run(weeklyProductMetricsJob, params)
    }

    /**
     * 매일 00:10 (KST) 실행 → 월간 집계: 리더에서 aggregateDate 기준으로 월간 윈도우 계산
     */
    @Scheduled(cron = "0 10 0 * * ?")
    fun productMetricsMonthlyJob() {
        val aggregateDate = LocalDate.now()
        val params = JobParametersBuilder()
            .addString("aggregateDate", aggregateDate.toString())
            .addString("version", "v1")
            .addString("batchType", BatchType.MONTHLY.name)
            .toJobParameters()
        jobLauncher.run(monthlyProductMetricsJob, params)
    }
}
