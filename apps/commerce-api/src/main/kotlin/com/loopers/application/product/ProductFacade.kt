package com.loopers.application.product

import com.loopers.domain.product.ProductService
import com.loopers.domain.stock.StockCommand
import com.loopers.domain.stock.StockService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductFacade(
    private val productService: ProductService,
    private val stockService: StockService,
) {

    @Transactional
    fun createProduct(criteria: ProductCriteria.Create): ProductCriteria.ProductInfo {
        val createdProduct = productService.createProduct(criteria.toCommand())
        val createdStock = stockService.createStock(StockCommand.Create(createdProduct.id, criteria.quantity ?: 0))
        return ProductCriteria.ProductInfo.from(createdProduct, createdStock)
    }
}
