package com.loopers.domain.order.vo

import com.loopers.domain.vo.Address
import com.loopers.domain.vo.Email
import com.loopers.domain.vo.Mobile

class OrderCustomerFixture {
    private var name = "홍길동"
    private var email = Email("shyoon991@gmail.com")
    private var mobile = Mobile("010-1234-5678")
    private var address = Address(
        "12345",
        "서울시 강남구",
        "테스트 도로 123",
    )

    companion object {
        fun anOrderCustomer(): OrderCustomerFixture = OrderCustomerFixture()
    }

    fun name(name: String): OrderCustomerFixture = apply { this.name = name }

    fun email(email: Email): OrderCustomerFixture = apply { this.email = email }

    fun mobile(mobile: Mobile): OrderCustomerFixture = apply { this.mobile = mobile }

    fun address(address: Address): OrderCustomerFixture = apply { this.address = address }

    fun build(): OrderCustomer = OrderCustomer(
        name,
        email,
        mobile,
        address,
    )
}
