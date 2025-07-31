package com.loopers.application.order

import com.loopers.domain.order.OrderCommand
import com.loopers.domain.order.vo.Quantity
import com.loopers.domain.vo.Address
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Mobile
import com.loopers.domain.vo.Price

class OrderCriteria {
    data class Create(
        val userId: Long,
        val ordererName: String,
        val ordererEmail: Email,
        val ordererMobile: Mobile,
        val ordererAddress: Address,
        val orderItems: List<OrderItemCriteria>,
    ) {

        init {
            require(ordererName.isNotBlank()) { "주문자 이름은 비어있을 수 없습니다." }
            require(orderItems.isNotEmpty()) { "주문 항목은 최소 하나 이상이어야 합니다." }
        }

        fun toCommand(): OrderCommand.Create {
            return OrderCommand.Create(
                userId,
                ordererName,
                ordererEmail,
                ordererMobile,
                ordererAddress,
                orderItems.map { it.toCommand() },
            )
        }

        data class OrderItemCriteria(
            val productId: Long,
            val productName: String,
            val quantity: Quantity,
            val totalPrice: Price,
            val amount: Price,
        ) {
            init {
                require(productId > 0) { "상품 아이디는 0보다 커야 합니다." }
            }

            fun toCommand(): OrderCommand.Create.OrderItemCommand {
                return OrderCommand.Create.OrderItemCommand(
                    productId,
                    productName,
                    quantity,
                    totalPrice,
                    amount,
                )
            }
        }
    }
}
