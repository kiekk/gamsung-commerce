package com.loopers.domain.order

import com.loopers.application.order.OrderCriteria
import com.loopers.domain.product.ProductEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType

class OrderTotalPriceCalculator {
    fun calculateTotalPrice(orderItems: List<OrderCriteria.Create.OrderItem>, products: List<ProductEntity>): Long {
        return orderItems.map { orderItem ->
            val product = products.find { it.id == orderItem.productId }
                ?: throw CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다. productId: ${orderItem.productId}")

            product.price.value * orderItem.quantity.value
        }.sumOf { it }
    }
}
