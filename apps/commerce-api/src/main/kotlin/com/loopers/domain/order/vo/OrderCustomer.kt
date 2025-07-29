package com.loopers.domain.order.vo

import com.loopers.domain.vo.Address
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Mobile
import jakarta.persistence.Embeddable

@Embeddable
data class OrderCustomer(
    val name: String,
    val email: Email,
    val mobile: Mobile,
    val address: Address,
) {
}
