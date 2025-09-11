package com.loopers.interfaces.scheduler.productrank

import com.loopers.domain.productrank.ProductRankService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class ProductRankBackupScheduler(
    private val productRankService: ProductRankService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "1 0 0 * * *", zone = "Asia/Seoul")
    fun backupYesterdayProductRank() {
        log.info("[ProductRankBackupScheduler.backupYesterdayProductRank] start")
        productRankService.backupYesterdayProductRank(LocalDate.now().minusDays(1))
        log.info("[ProductRankBackupScheduler.backupYesterdayProductRank] end")
    }

    @Scheduled(cron = "1 0 * * * *", zone = "Asia/Seoul")
    fun backupPrevHourProductRank() {
        log.info("[ProductRankBackupScheduler.backupPrevHourProductRank] start")
        productRankService.backupPrevHourProductRank(LocalDateTime.now())
        log.info("[ProductRankBackupScheduler.backupPrevHourProductRank] end")
    }
}
