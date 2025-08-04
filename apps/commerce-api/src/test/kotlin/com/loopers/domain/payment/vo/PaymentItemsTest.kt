package com.loopers.domain.payment.vo

import com.loopers.domain.payment.PaymentItemEntity
import com.loopers.domain.payment.fixture.PaymentEntityFixture.Companion.aPayment
import com.loopers.domain.vo.Price
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PaymentItemsTest {
    /*
     **🧱 단위 테스트**
    - [ ] 결제 항목 목록이 비어있으면 총 금액은 0이다.
    - [ ] 결제 항목 목록은 총 금액을 계산할 수 있다.
     */
    @DisplayName("PaymentItems를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("결제 항목 목록이 비어있으면 예외를 던진다.")
        @Test
        fun failWhenItemsIsEmpty() {
            // arrange
            val items = emptyList<PaymentItemEntity>()

            // act
            val paymentItems = PaymentItems(items)

            // assert
            assertThat(paymentItems.totalAmount()).isEqualTo(Price.ZERO)
        }

        @DisplayName("결제 항목 목록은 총 금액을 계산할 수 있다.")
        @Test
        fun calculateTotalAmount() {
            // arrange
            val payment = aPayment().build()
            val paymentItem1 = PaymentItemEntity(payment, 1L, Price(1000L))
            val paymentItem2 = PaymentItemEntity(payment, 2L, Price(2000L))

            // act
            val paymentItems = PaymentItems(listOf(paymentItem1, paymentItem2))

            // assert
            assertThat(paymentItems.totalAmount()).isEqualTo(Price(3000L))
        }
    }
}
