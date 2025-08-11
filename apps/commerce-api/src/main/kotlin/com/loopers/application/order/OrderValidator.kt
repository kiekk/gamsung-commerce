package com.loopers.application.order

import com.loopers.domain.coupon.IssuedCouponValidationService
import com.loopers.domain.product.ProductValidationService
import com.loopers.domain.stock.StockValidationService
import org.springframework.stereotype.Component

@Component
class OrderValidator(
    private val productValidationService: ProductValidationService,
    private val stockValidationService: StockValidationService,
    private val issuedCouponValidationService: IssuedCouponValidationService,
) {

    fun validate(criteria: OrderCriteria.Create) {
        criteria.orderItems.forEach { orderItem ->
            productValidationService.validate(orderItem.productId)
            stockValidationService.validate(orderItem.productId, orderItem.quantity.value)
        }
        criteria.issuedCouponId?.let { issuedCouponValidationService.validate(it) }
    }
}
