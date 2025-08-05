package com.loopers.domain.stock

import com.loopers.domain.BaseEntityWithoutId
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Entity
@Table(name = "stock")
class StockEntity(
    @Id
    val productId: Long = 0L,
    var quantity: Int,
) : BaseEntityWithoutId() {

    @Version
    var version: Long? = null

    init {
        require(productId > 0) { "상품 ID는 1 이상이어야 합니다." }
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
