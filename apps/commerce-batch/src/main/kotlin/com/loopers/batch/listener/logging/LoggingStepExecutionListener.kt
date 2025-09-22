package com.loopers.batch.listener.logging

import org.slf4j.LoggerFactory
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.stereotype.Component

@Component
class LoggingStepExecutionListener : StepExecutionListener {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun beforeStep(stepExecution: StepExecution) {
        log.info(
            "[StepListener] START - Step: {}, Job: {}",
            stepExecution.stepName,
            stepExecution.jobExecution.jobInstance.jobName,
        )
    }

    override fun afterStep(stepExecution: StepExecution): ExitStatus {
        log.info(
            "[StepListener] END   - Step: {}, Job: {}, Status: {}",
            stepExecution.stepName,
            stepExecution.jobExecution.jobInstance.jobName,
            stepExecution.exitStatus.exitCode,
        )
        return stepExecution.exitStatus
    }
}
