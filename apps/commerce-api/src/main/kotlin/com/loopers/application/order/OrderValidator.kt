package com.loopers.application.order

import com.loopers.domain.coupon.IssuedCouponValidationService
import com.loopers.domain.order.OrderCommand
import com.loopers.domain.product.ProductValidationService
import com.loopers.domain.stock.StockValidationService
import org.springframework.stereotype.Component

@Component
class OrderValidator(
    private val productValidationService: ProductValidationService,
    private val stockValidationService: StockValidationService,
    private val issuedCouponValidationService: IssuedCouponValidationService,
) {

    fun validate(command: OrderCommand.Create) {
        command.orderItems.forEach { orderItem ->
            productValidationService.validate(orderItem.productId)
            stockValidationService.validate(orderItem.productId, orderItem.quantity.value)
        }
        command.issuedCouponId?.let { issuedCouponValidationService.validate(it) }
    }
}
