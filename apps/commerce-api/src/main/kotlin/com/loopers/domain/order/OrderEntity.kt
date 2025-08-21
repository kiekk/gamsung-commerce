package com.loopers.domain.order

import com.loopers.domain.BaseEntity
import com.loopers.domain.order.vo.OrderCustomer
import com.loopers.domain.order.vo.OrderItems
import com.loopers.domain.vo.Price
import com.loopers.support.enums.order.OrderStatusType
import jakarta.persistence.AttributeOverride
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "orders")
class OrderEntity(
    val userId: Long,
    @Embedded
    val orderCustomer: OrderCustomer,
    @Embedded
    var discountPrice: Price = Price.ZERO,
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    private val _orderItems: MutableList<OrderItemEntity> = mutableListOf(),
    val issuedCouponId: Long? = null,
    val orderKey: String? = null,
) : BaseEntity() {
    val orderItems: OrderItems
        get() = OrderItems(_orderItems)

    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatusType = OrderStatusType.PENDING

    @Embedded
    @AttributeOverride(name = "value", column = Column("total_price"))
    var totalPrice: Price = Price.ZERO
        private set

    @Embedded
    @AttributeOverride(name = "value", column = Column("amount"))
    var amount: Price = Price.ZERO
        private set

    fun complete() {
        orderStatus = OrderStatusType.COMPLETED
    }

    fun cancel() {
        orderStatus = OrderStatusType.CANCELED
    }

    fun fail() {
        orderStatus = OrderStatusType.FAILED
    }

    fun addItems(orderItems: List<OrderItemEntity>) {
        _orderItems.addAll(orderItems)
        totalPrice = this.orderItems.amount()
        amount = Price(this.orderItems.amount().value - discountPrice.value)
    }

    fun isNotEqualAmount(totalPrice: Price): Boolean {
        return amount != totalPrice
    }

    fun isCompleted(): Boolean {
        return orderStatus == OrderStatusType.COMPLETED
    }
}
