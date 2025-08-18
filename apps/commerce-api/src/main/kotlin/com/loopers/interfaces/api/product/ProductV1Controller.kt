package com.loopers.interfaces.api.product

import com.loopers.application.product.ProductFacade
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/products")
class ProductV1Controller(
    private val productFacade: ProductFacade,
) : ProductV1ApiSpec {

    @PostMapping("")
    override fun createProduct(
        @RequestBody request: ProductV1Dto.CreateRequest,
        httpServletRequest: HttpServletRequest,
    ): ApiResponse<ProductV1Dto.ProductResultResponse> {
        val username = httpServletRequest.getHeader("X-USER-ID")
            ?: throw CoreException(ErrorType.BAD_REQUEST, "X-USER-ID가 존재하지 않습니다.")
        return productFacade.createProduct(request.toCriteria(username))
            .let { ProductV1Dto.ProductResultResponse.from(it) }
            .let { ApiResponse.success(it) }
    }

    @GetMapping("/{productId}")
    override fun getProduct(@PathVariable("productId") productId: Long): ApiResponse<ProductV1Dto.ProductDetailResponse> {
        return productFacade.getProduct(productId)
            .let { ProductV1Dto.ProductDetailResponse.from(it) }
            .let { ApiResponse.success(it) }
    }

    @GetMapping("")
    override fun getProducts(
        request: ProductV1Dto.SearchRequest,
        pageable: Pageable,
    ): ApiResponse<Page<ProductV1Dto.ProductListResponse>> {
        return productFacade.searchProducts(request.toCriteria(), pageable)
            .map { ProductV1Dto.ProductListResponse.from(it) }
            .let { ApiResponse.success(it) }
    }

    @GetMapping("count-query")
    override fun getProductsByCountQuery(
        request: ProductV1Dto.SearchRequest,
        pageable: Pageable,
    ): ApiResponse<Page<ProductV1Dto.ProductListResponse>> {
        return productFacade.searchProductsByCountQuery(pageable)
            .map { ProductV1Dto.ProductListResponse.from(it) }
            .let { ApiResponse.success(it) }
    }
}
