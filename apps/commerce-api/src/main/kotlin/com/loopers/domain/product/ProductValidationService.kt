package com.loopers.domain.product

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component

@Component
class ProductValidationService(
    private val productRepository: ProductRepository,
) {
    fun validate(productId: Long) {
        val product = productRepository.findById(productId)
            ?: throw CoreException(
                ErrorType.NOT_FOUND,
                "존재하지 않는 상품입니다. productId: $productId",
            )

        product.isNotActive() && throw CoreException(
            ErrorType.CONFLICT,
            "주문 가능한 상태가 아닌 상품입니다. productId: $productId, 상태: ${product.status}",
        )
    }
}
