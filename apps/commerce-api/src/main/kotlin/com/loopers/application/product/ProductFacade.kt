package com.loopers.application.product

import com.loopers.domain.product.ProductService
import com.loopers.domain.stock.StockService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductFacade(
    private val productService: ProductService,
    private val stockService: StockService,
) {

    @Transactional
    fun createProduct(command: ProductCommand.Create): ProductCommand.ProductInfo {
        val createdProduct = productService.createProduct(command.toProductEntity())
        val createdStock = stockService.createStock(command.toStockEntity(createdProduct.id))
        return ProductCommand.ProductInfo.from(createdProduct, createdStock)
    }
}
