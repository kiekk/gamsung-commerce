package com.loopers.application.order

import com.loopers.domain.order.OrderEntity
import com.loopers.domain.vo.Price

class OrderInfo {

    data class OrderDetail(
        val orderId: Long,
        val userId: Long,
        val ordererName: String,
        val ordererEmail: String,
        val ordererMobile: String,
        val ordererZipCode: String,
        val ordererAddress: String,
        val ordererAddressDetail: String? = null,
        val orderItemCount: Int,
        val totalPrice: Price,
        val orderKey: String? = null,
    ) {

        companion object {
            fun from(orderEntity: OrderEntity): OrderDetail = OrderDetail(
                orderEntity.id,
                orderEntity.userId,
                orderEntity.orderCustomer.name,
                orderEntity.orderCustomer.email.value,
                orderEntity.orderCustomer.mobile.value,
                orderEntity.orderCustomer.address.zipCode,
                orderEntity.orderCustomer.address.address,
                orderEntity.orderCustomer.address.addressDetail,
                orderEntity.orderItems.size(),
                orderEntity.totalPrice,
                orderEntity.orderKey,
            )
        }
    }
}
