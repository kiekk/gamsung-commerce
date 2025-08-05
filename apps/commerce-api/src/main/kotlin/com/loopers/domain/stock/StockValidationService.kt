package com.loopers.domain.stock

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class StockValidationService(
    private val stockRepository: StockRepository,
) {

    fun validate(productId: Long, quantity: Int) {
        val stock = stockRepository.findByProductId(productId)
            ?: throw CoreException(
                ErrorType.NOT_FOUND,
                "재고를 찾을 수 업습니다. productId: $productId",
            )

        if (stock.isQuantityLessThan(quantity)) {
            throw CoreException(
                ErrorType.CONFLICT,
                "재고가 부족한 상품입니다. productId: $productId, 요청 수량: $quantity, 재고: ${stock.quantity}",
            )
        }
    }
}
