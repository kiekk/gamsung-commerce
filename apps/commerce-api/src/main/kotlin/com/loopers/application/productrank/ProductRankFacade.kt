package com.loopers.application.productrank

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class ProductRankFacade(
    private val productRankHandlerFactory: ProductRankHandlerFactory,
) {

    private val log = LoggerFactory.getLogger(ProductRankFacade::class.java)

    fun getProductRanks(criteria: ProductRankCriteria.Search, pageable: Pageable): Page<ProductRankInfo.ProductRankList> {
        log.info("[ProductRankFacade.getProductRanks] criteria: {}, pageable: {}", criteria, pageable)
        val rankHandlerCriteria = criteria.toProductRankHandlerCriteria(pageable.offset, pageable.pageSize)
        val productRanks =
            productRankHandlerFactory.gerProductRanks(rankHandlerCriteria)
        val totalCount = productRankHandlerFactory.getTotalCount(rankHandlerCriteria)
        return PageImpl(productRanks, pageable, totalCount)
    }
}
