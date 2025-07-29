package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import com.loopers.domain.order.vo.Quantity
import com.loopers.domain.vo.Price

class OrderItemEntity(
    val productId: Long,
    productName: String,
    val quantity: Quantity,
    val totalPrice: Price,
    val amount: Price,
) : BaseEntity() {

    init {
        require(productId > 0) { "상품 아이디는 0보다 커야 합니다." }
        require(productName.isNotBlank()) { "상품 이름은 비어있을 수 없습니다." }
    }
}
