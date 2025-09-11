package com.loopers.interfaces.scheduler.productrank

import com.loopers.domain.productrank.ProductRankCarryOverService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ProductRankScheduler(
    private val productRankCarryOverService: ProductRankCarryOverService,
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 50 11 * * *", zone = "Asia/Seoul")
    fun carryOverTomorrowRank() {
        log.info("[ProductRankScheduler.carryOverTomorrowRank] start")
        // 상위 20개 상품 랭킹을 carry-over, 점수는 1% 감소
        productRankCarryOverService.carryOverTomorrowRank(20, 0.01)
        log.info("[ProductRankScheduler.carryOverTomorrowRank] end")
    }

    @Scheduled(cron = "0 50 * * * *", zone = "Asia/Seoul")
    fun carryOverNextHourRank() {
        log.info("[ProductRankScheduler.carryOverNextHourRank] start")
        // 상위 20개 상품 랭킹을 carry-over, 점수는 1% 감소
        productRankCarryOverService.carryOverNextHourRank(20, 0.01)
        log.info("[ProductRankScheduler.carryOverNextHourRank] end")
    }
}
