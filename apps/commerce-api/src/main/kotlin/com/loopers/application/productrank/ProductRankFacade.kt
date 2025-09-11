package com.loopers.application.productrank

import com.loopers.domain.product.query.ProductQueryService
import com.loopers.domain.productrank.ProductRankService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ProductRankFacade(
    private val productRankService: ProductRankService,
    private val productQueryService: ProductQueryService,
) {

    private val log = LoggerFactory.getLogger(ProductRankFacade::class.java)

    fun getProductRanksByDay(criteria: ProductRankCriteria.SearchDay, pageable: Pageable): Page<ProductRankInfo.ProductRankList> {
        log.info("[ProductRankFacade.getProductRanksByDay] criteria: {}, pageable: {}", criteria, pageable)
        val productRankMap = productRankService.getProductRankIdsByDay(criteria.toCommand(pageable.offset, pageable.pageSize))

        if (productRankMap.isEmpty()) {
            return PageImpl(emptyList(), pageable, 0)
        }

        val products = productQueryService.getProductsByIds(productRankMap.keys.map { it.toLong() }.toList())
            ?: emptyList()
        // 랭크 정보 추가
        val productRanks = products
            .map {
                ProductRankInfo.ProductRankList.from(
                    it,
                    productRankMap[it.id.toString()]?.rank,
                    productRankMap[it.id.toString()]?.score,
                )
            }
            .sortedBy { it.rankNumber }
            .toList()
        // 총 개수 조회
        val productRankTotalCount = productRankService.getProductRankTotalCountByDay(criteria.rankDate)

        return PageImpl(productRanks, pageable, productRankTotalCount)
    }

    fun getProductRanksByHour(
        criteria: ProductRankCriteria.SearchHour,
        pageable: Pageable,
    ): Page<ProductRankInfo.ProductRankList> {
        log.info("[ProductRankFacade.getProductRanksByHour] criteria: {}, pageable: {}", criteria, pageable)
        val productRankMap = productRankService.getProductRankIdsByHour(criteria.toCommand(pageable.offset, pageable.pageSize))

        if (productRankMap.isEmpty()) {
            return PageImpl(emptyList(), pageable, 0)
        }

        val products = productQueryService.getProductsByIds(productRankMap.keys.map { it.toLong() }.toList())
            ?: emptyList()
        // 랭크 정보 추가
        val productRanks = products
            .map {
                ProductRankInfo.ProductRankList.from(
                    it,
                    productRankMap[it.id.toString()]?.rank,
                    productRankMap[it.id.toString()]?.score,
                )
            }
            .sortedBy { it.rankNumber }
            .toList()
        // 총 개수 조회
        val productRankTotalCount = productRankService.getProductRankTotalCountByHour(criteria.rankDate)

        return PageImpl(productRanks, pageable, productRankTotalCount)
    }
}
