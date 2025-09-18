package com.loopers.interfaces.batch.api.productmetrics

import com.loopers.support.enums.batch.BatchType
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/batch/product-metrics")
class ProductMetricsV1BatchController(
    private val jobLauncher: JobLauncher,
    @Qualifier("weeklyProductMetricsJob") private val weeklyProductMetricsJob: Job,
    @Qualifier("monthlyProductMetricsJob") private val monthlyProductMetricsJob: Job,
) {
    @PostMapping("/weekly")
    fun productMetricsWeeklyJob(@RequestParam aggregateDate: LocalDate) {
        val params = JobParametersBuilder()
            .addString("aggregateDate", aggregateDate.toString())
            .addString("version", "v1")
            .addString("batchType", BatchType.WEEKLY.name)
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters()
        jobLauncher.run(weeklyProductMetricsJob, params)
    }

    @PostMapping("/monthly")
    fun productMetricsMonthlyJob(@RequestParam aggregateDate: LocalDate) {
        val params = JobParametersBuilder()
            .addString("aggregateDate", aggregateDate.toString())
            .addString("version", "v1")
            .addString("batchType", BatchType.MONTHLY.name)
            .addLong("run.id", System.currentTimeMillis())
            .toJobParameters()
        jobLauncher.run(monthlyProductMetricsJob, params)
    }
}
