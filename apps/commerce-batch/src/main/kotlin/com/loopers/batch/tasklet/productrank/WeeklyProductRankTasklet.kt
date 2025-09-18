package com.loopers.batch.tasklet.productrank

import com.loopers.domain.product.ProductQueryService
import com.loopers.domain.productmetrics.ProductMetricsWeeklyRepository
import com.loopers.domain.productrank.mv.MvProductRankWeekly
import com.loopers.domain.productrank.mv.MvProductRankWeeklyRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
@StepScope
class WeeklyProductRankTasklet(
    private val productMetricsWeeklyRepository: ProductMetricsWeeklyRepository,
    private val mvProductRankWeeklyRepository: MvProductRankWeeklyRepository,
    private val productQueryService: ProductQueryService,
    @Value("#{jobParameters['aggregateDate']}") private val aggregateDateParam: String?,
) : Tasklet {

    private val aggregateDate = aggregateDateParam?.let { LocalDate.parse(it) }
        ?: throw IllegalArgumentException("aggregateDate는 필수입니다.")
    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val top100 = productMetricsWeeklyRepository.findTopByAggregateDateOrderByScoreDesc(aggregateDate, TOP_N_SIZE)

        val productListModelMap = productQueryService.getProductsByIds(top100.map { it.productId })
            ?.associateBy { it.id }
            ?: emptyMap()

        val ranked = top100.mapIndexed { idx, productMetrics ->
            val productModel = productListModelMap[productMetrics.productId]
                ?: throw IllegalStateException("productId=${productMetrics.productId}에 해당하는 상품 정보를 찾을 수 없습니다.")
            MvProductRankWeekly(
                productMetrics.productId,
                aggregateDate,
                idx + 1L,
                productModel.name,
                productModel.price,
                productModel.productStatus,
                productModel.brandName,
                productMetrics.viewCount,
                LocalDateTime.now(),
            )
        }

        // 기존 데이터 삭제
        mvProductRankWeeklyRepository.deleteByAggregateDate(aggregateDate)

        mvProductRankWeeklyRepository.saveAll(ranked)
        log.info("aggregateDate={}에 대해 mv_product_rank_weekly에 {}개의 랭킹 데이터 저장 완료", aggregateDate, ranked.size)
        return RepeatStatus.FINISHED
    }

    companion object {
        const val TOP_N_SIZE = 100
    }
}
