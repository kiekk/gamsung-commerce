package com.loopers.application.product

import com.loopers.domain.brand.BrandService
import com.loopers.domain.product.ProductService
import com.loopers.domain.productlike.ProductLikeService
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductFacade(
    private val productService: ProductService,
    private val stockService: StockService,
    private val brandService: BrandService,
    private val productLikeService: ProductLikeService,
) {

    @Transactional
    fun createProduct(criteria: ProductCriteria.Create): ProductInfo.ProductResult {
        val createdProduct = productService.createProduct(criteria.toCommand())
        val createdStock = stockService.createStock(StockCommand.Create(createdProduct.id, criteria.quantity ?: 0))
        return ProductInfo.ProductResult.from(createdProduct, createdStock)
    }

    @Transactional(readOnly = true)
    fun getProduct(id: Long): ProductInfo.ProductDetail {
        val product = productService.findProductBy(id) ?: throw CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다. $id")
        val brand = brandService.findBrandBy(product.brandId) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "브랜드를 찾을 수 없습니다. ${product.brandId}",
        )
        val productLikeCount = productLikeService.getProductLikeCount(product.id)
        return ProductInfo.ProductDetail.from(product, brand, productLikeCount)
    }
}
