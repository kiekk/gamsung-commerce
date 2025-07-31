package com.loopers.domain.payment

import com.loopers.domain.payment.fixture.PaymentEntityFixture.Companion.aPayment
import com.loopers.domain.vo.Price
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PaymentEntityTest {
    /*
     **🧱 단위 테스트**
    - [ ] 결제 정보를 생성하면 결제 금액과 결제 항목의 총 금액은 일치해야 한다.
    - [ ] 결제 정볼르 생성하면 상태는 PENDING로 초기화된다.
    - [ ] 결제 완료 처리 시 상태는 COMPLETED로 변경된다.
    - [ ] 결제 실패 처리 시 상태는 FAILED로 변경된다.
     */
    @DisplayName("PaymentEntity를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("결제 정보를 생성하면 결제 금액과 결제 항목의 총 금액은 일치해야 한다.")
        @Test
        fun totalAmountMatchesPaymentItems() {
            // arrange
            val payment = aPayment().build()
            val paymentItem1 = PaymentItemEntity(payment, 1L, Price(1000L))
            val paymentItem2 = PaymentItemEntity(payment, 2L, Price(2000L))

            // act
            payment.addItems(listOf(paymentItem1, paymentItem2))

            // assert
            assertThat(payment.totalAmount).isEqualTo(Price(3000L))
        }

        @DisplayName("결제 정보를 생성하면 상태는 PENDING로 초기화된다.")
        @Test
        fun initialStatusIsPending() {
            // arrange
            val payment = aPayment().build()
            val paymentItem = PaymentItemEntity(payment, 1L, Price(1000L))

            // act
            payment.addItems(listOf(paymentItem))

            // assert
            assertThat(payment.status).isEqualTo(PaymentEntity.PaymentStatusType.PENDING)
        }

        @DisplayName("결제 완료 처리 시 상태는 COMPLETED로 변경된다.")
        @Test
        fun completePaymentChangesStatusToCompleted() {
            // arrange
            val payment = PaymentEntity(1L, PaymentEntity.PaymentMethodType.POINT)
            val paymentItem = PaymentItemEntity(payment, 1L, Price(1000L))
            payment.addItems(listOf(paymentItem))

            // act
            payment.complete()

            // assert
            assertThat(payment.status).isEqualTo(PaymentEntity.PaymentStatusType.COMPLETED)
        }

        @DisplayName("결제 실패 처리 시 상태는 FAILED로 변경된다.")
        @Test
        fun failPaymentChangesStatusToFailed() {
            // arrange
            val payment = PaymentEntity(1L, PaymentEntity.PaymentMethodType.POINT)
            val paymentItem = PaymentItemEntity(payment, 1L, Price(1000L))
            payment.addItems(listOf(paymentItem))

            // act
            payment.fail()

            // assert
            assertThat(payment.status).isEqualTo(PaymentEntity.PaymentStatusType.FAILED)
        }
    }
}
