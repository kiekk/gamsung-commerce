package com.loopers.domain.order

import com.loopers.domain.order.vo.OrderCustomer
import com.loopers.domain.vo.Address
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Mobile
import com.loopers.domain.vo.Price
import com.loopers.domain.vo.Quantity

class OrderCommand {
    data class Create(
        val userId: Long,
        val ordererName: String,
        val ordererEmail: Email,
        val ordererMobile: Mobile,
        val ordererAddress: Address,
        val orderItems: List<OrderItemCommand>,
        val discountAmount: Long = 0L,
        val issuedCouponId: Long? = null,
    ) {

        init {
            require(ordererName.isNotBlank()) { "주문자 이름은 비어있을 수 없습니다." }
            require(orderItems.isNotEmpty()) { "주문 항목은 최소 하나 이상이어야 합니다." }
        }

        data class OrderItemCommand(
            val productId: Long,
            val productName: String,
            val quantity: Quantity,
            val amount: Price,
        ) {
            init {
                require(productId > 0) { "상품 아이디는 0보다 커야 합니다." }
            }
        }

        fun toOrderEntity(): OrderEntity {
            return OrderEntity(
                userId,
                OrderCustomer(
                    ordererName,
                    ordererEmail,
                    ordererMobile,
                    ordererAddress,
                ),
                Price(discountAmount),
                issuedCouponId = issuedCouponId,
            )
        }

        fun toOrderItemEntities(order: OrderEntity): List<OrderItemEntity> {
            return orderItems.flatMap { command ->
                List(command.quantity.value) {
                    OrderItemEntity(
                        order,
                        command.productId,
                        command.productName,
                        command.amount,
                    )
                }
            }
        }
    }
}
