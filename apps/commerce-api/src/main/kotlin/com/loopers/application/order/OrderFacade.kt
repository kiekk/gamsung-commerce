package com.loopers.application.order

import com.loopers.domain.order.OrderService
import com.loopers.domain.product.ProductService
import com.loopers.domain.stock.StockService
import com.loopers.domain.user.UserService
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderFacade(
    private val userService: UserService,
    private val productService: ProductService,
    private val stockService: StockService,
    private val orderService: OrderService,
) {
    @Transactional
    fun placeOrder(criteria: OrderCriteria.Create): Long {
        userService.findUserBy(criteria.userId) ?: throw CoreException(
            ErrorType.NOT_FOUND,
            "사용자를 찾을 수 없습니다. userId: ${criteria.userId}",
        )

        validateOrderItems(criteria.orderItems)
        val createdOrder = orderService.createOrder(criteria.toCommand())

        return createdOrder.id
    }

    private fun validateOrderItems(orderItems: List<OrderCriteria.Create.OrderItemCriteria>) {
        val productIds = orderItems.map { it.productId }.distinct()
        val products = productService.getProductsByIds(productIds)
        val stocks = stockService.getStocksByProductIds(productIds)

        val productMap = products.associateBy { it.id }
        val stockMap = stocks.associateBy { it.productId }

        orderItems.forEach { orderItem ->
            val product = productMap[orderItem.productId]
                ?: run {
                    throw CoreException(
                        ErrorType.NOT_FOUND,
                        "존재하지 않는 상품입니다. productId: ${orderItem.productId}",
                    )
                }

            if (product.isNotActive()) {
                throw CoreException(
                    ErrorType.CONFLICT,
                    "주문 가능한 상태가 아닌 상품입니다. productId: ${orderItem.productId}, 상태: ${product.status}",
                )
            }

            val stock = stockMap[orderItem.productId]
            if (stock == null || stock.invalidQuantity(orderItem.quantity)) {
                throw CoreException(
                    ErrorType.CONFLICT,
                    "재고가 부족한 상품입니다. productId: ${orderItem.productId}, 요청 수량: ${orderItem.quantity}, 재고: ${stock?.quantity ?: 0}",
                )
            }
        }
    }


}
