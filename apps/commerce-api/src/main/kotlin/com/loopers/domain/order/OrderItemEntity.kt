package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import com.loopers.domain.vo.Price
import jakarta.persistence.AttributeOverride
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "order_item")
class OrderItemEntity(
    @ManyToOne
    val order: OrderEntity,
    val productId: Long,
    productName: String,
    @Embedded
    @AttributeOverride(name = "value", column = Column("amount"))
    val amount: Price,
) : BaseEntity() {

    init {
        require(productId > 0) { "상품 아이디는 0보다 커야 합니다." }
        require(productName.isNotBlank()) { "상품 이름은 비어있을 수 없습니다." }
    }
}
