package com.loopers.domain.stock

import com.loopers.support.error.ErrorType
import com.loopers.support.error.payment.PaymentException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StockService(
    private val stockRepository: StockRepository,
) {
    @Transactional
    fun createStock(command: StockCommand.Create): StockEntity {
        return stockRepository.save(command.toEntity())
    }

    @Transactional(readOnly = true)
    fun getStocksByProductIds(productIds: List<Long>): List<StockEntity> {
        return stockRepository.findByIds(productIds)
    }

    @Transactional
    fun deductStockQuantities(command: List<StockCommand.Decrease>) {
        val decreaseCommandMap = command.associate { it.productId to it.quantity }
        stockRepository.findByIds(command.map { it.productId })
            .associateBy { it.productId }
            .forEach { (productId, stock) ->
                decreaseCommandMap[productId]?.let { quantity ->
                    stock.isQuantityLessThan(quantity) && throw PaymentException(
                        ErrorType.CONFLICT,
                        "재고가 부족합니다. productId: $productId, 요청 수량: $quantity, 현재 재고: ${stock.quantity}",
                    )
                    stock.deductQuantity(quantity)
                }
            }
    }
}
