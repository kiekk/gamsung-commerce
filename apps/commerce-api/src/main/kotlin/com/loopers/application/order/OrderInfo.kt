package com.loopers.application.order

import com.loopers.domain.order.OrderEntity
import com.loopers.domain.vo.Address
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Mobile
import com.loopers.domain.vo.Price

class OrderInfo {

    data class OrderDetail(
        val orderId: Long,
        val ordererName: String,
        val ordererEmail: Email,
        val ordererMobile: Mobile,
        val ordererAddress: Address,
        val orderItemCount: Int,
        val totalPrice: Price,
    ) {

        companion object {
            fun from(orderEntity: OrderEntity): OrderDetail = OrderDetail(
                orderEntity.id,
                orderEntity.orderCustomer.name,
                orderEntity.orderCustomer.email,
                orderEntity.orderCustomer.mobile,
                orderEntity.orderCustomer.address,
                orderEntity.orderItems.size(),
                orderEntity.totalPrice,
            )
        }
    }
}
