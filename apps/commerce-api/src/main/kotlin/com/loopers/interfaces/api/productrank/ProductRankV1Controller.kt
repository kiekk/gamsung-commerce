package com.loopers.interfaces.api.productrank

import com.loopers.application.productrank.ProductRankFacade
import com.loopers.interfaces.api.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/rankings")
class ProductRankV1Controller(
    private val productRankFacade: ProductRankFacade,
) : ProductRankV1ApiSpec {

    @GetMapping("")
    override fun getProductRanks(
        request: ProductRankV1Dto.Request,
        pageable: Pageable,
    ): ApiResponse<Page<ProductRankV1Dto.ProductRankListResponse>> {
        return productRankFacade.getProductRanksByDay(request.toCriteria(), pageable)
            .map { ProductRankV1Dto.ProductRankListResponse.from(it) }
            .let { ApiResponse.success(it) }
    }
}
