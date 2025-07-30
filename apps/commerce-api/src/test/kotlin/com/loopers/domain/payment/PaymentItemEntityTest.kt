package com.loopers.domain.payment

import com.loopers.domain.vo.Price
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PaymentItemEntityTest {
    /*
    **🧱 단위 테스트**
    - [ ] 결제 항목을 생성하면 상태는 PENDING로 초기화된다.
    - [ ] 결제 항목은 결제가 완료되면 상태가 COMPLETED로 변경된다.
    - [ ] 결제 항목은 결제가 실패하면 상태가 FAILED로 변경된다.
    - [ ] 결제 항목은 정상적으로 생성되면 결제 ID, 주문 항목 ID, 금액이 올바르게 설정된다.
     */
    @DisplayName("PaymentItemEntity를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("결제 항목을 생성하면 상태는 PENDING로 초기화된다.")
        @Test
        fun createPaymentItemEntityWithPendingStatus() {
            // arrange
            val paymentItem = PaymentItemEntity(
                1L,
                1L,
                Price(1000L),
            )

            // assert
            Assertions.assertThat(paymentItem.status).isEqualTo(PaymentItemEntity.PaymentItemStatusType.PENDING)
        }

        @DisplayName("결제 항목은 결제가 완료되면 상태가 COMPLETED로 변경된다.")
        @Test
        fun completePaymentItemEntity() {
            // arrange
            val paymentItem = PaymentItemEntity(
                1L,
                1L,
                Price(1000L),
            )

            // act
            paymentItem.complete()

            // assert
            assertThat(paymentItem.status).isEqualTo(PaymentItemEntity.PaymentItemStatusType.COMPLETED)
        }

        @DisplayName("결제 항목은 결제가 실패하면 상태가 FAILED로 변경된다.")
        @Test
        fun failPaymentItemEntity() {
            // arrange
            val paymentItem = PaymentItemEntity(
                1L,
                1L,
                Price(1000L),
            )

            // act
            paymentItem.fail()

            // assert
            assertThat(paymentItem.status).isEqualTo(PaymentItemEntity.PaymentItemStatusType.FAILED)
        }

        @DisplayName("결제 항목은 정상적으로 생성되면 결제 ID, 주문 항목 ID, 금액이 올바르게 설정된다.")
        @Test
        fun createPaymentItemEntityWithCorrectValues() {
            // arrange
            val paymentId = 1L
            val orderItemId = 1L
            val amount = Price(1000L)

            // act
            val paymentItem = PaymentItemEntity(
                paymentId,
                orderItemId,
                amount,
            )

            // assert
            assertThat(paymentItem.paymentId).isEqualTo(paymentId)
            assertThat(paymentItem.orderItemId).isEqualTo(orderItemId)
            assertThat(paymentItem.amount).isEqualTo(amount)
        }
    }

}
