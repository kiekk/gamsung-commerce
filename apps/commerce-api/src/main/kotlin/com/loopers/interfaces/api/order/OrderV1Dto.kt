package com.loopers.interfaces.api.order

import com.loopers.application.order.OrderCriteria
import com.loopers.application.order.OrderInfo
import com.loopers.domain.vo.Address
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Mobile
import com.loopers.domain.vo.Price
import com.loopers.domain.vo.Quantity
import com.loopers.support.enums.payment.PaymentMethodType

class OrderV1Dto {
    data class CreateRequest(
        val ordererName: String,
        val ordererEmail: String,
        val ordererMobile: String,
        val ordererZipCode: String,
        val ordererAddress: String,
        val ordererAddressDetail: String? = null,
        val orderItems: List<OrderItemRequest>,
        val paymentMethodType: PaymentMethodType,
        val issuedCouponId: Long? = null,
    ) {

        data class OrderItemRequest(
            val productId: Long,
            val quantity: Int,
        )

        fun toCriteria(username: String): OrderCriteria.Create {
            return OrderCriteria.Create(
                username,
                ordererName,
                Email(ordererEmail),
                Mobile(ordererMobile),
                Address(
                    ordererZipCode,
                    ordererAddress,
                    ordererAddressDetail,
                ),
                orderItems.map { OrderCriteria.Create.OrderItem(it.productId, Quantity(it.quantity)) },
                paymentMethodType,
                issuedCouponId,
            )
        }
    }

    data class OrderResponse(
        val orderId: Long,
        val ordererName: String,
        val ordererEmail: String,
        val ordererMobile: String,
        val ordererZipCode: String,
        val ordererAddress: String,
        val ordererAddressDetail: String? = null,
        val orderItemCount: Int,
        val totalPrice: Price,
    ) {
        companion object {
            fun from(orderDetail: OrderInfo.OrderDetail): OrderResponse = OrderResponse(
                orderDetail.orderId,
                orderDetail.ordererName,
                orderDetail.ordererEmail,
                orderDetail.ordererMobile,
                orderDetail.ordererZipCode,
                orderDetail.ordererAddress,
                orderDetail.ordererAddressDetail,
                orderDetail.orderItemCount,
                orderDetail.totalPrice,
            )
        }
    }
}
