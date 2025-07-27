package com.loopers.domain.stock

import com.loopers.domain.BaseEntity

class StockEntity(
    val productId: Long,
    var quantity: Int,
) : BaseEntity() {

    init {
        require(quantity >= 0) { "재고는 0 이상이어야 합니다." }
    }

    fun isQuantityLessThan(quantity: Int): Boolean {
        return this.quantity < quantity
    }

    fun deductQuantity(quantity: Int) {
        require(!isQuantityLessThan(quantity)) { "차감할 재고 수량이 없습니다." }
        this.quantity -= quantity
    }
}
