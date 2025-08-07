package com.loopers.domain.payment

import com.loopers.domain.payment.fixture.PaymentEntityFixture.Companion.aPayment
import com.loopers.domain.vo.Price
import com.loopers.support.enums.payment.PaymentStatusType
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
    - [ ] 결제 취소 처리 시 상태는 CANCELED로 변경된다.
     */
    @DisplayName("PaymentEntity를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("결제 정보를 생성하면 결제 금액과 결제 항목의 총 금액은 일치해야 한다.")
        @Test
        fun totalAmountMatchesPaymentItems() {
            // arrange

            // act
            val payment = aPayment().totalPrice(Price(3000L)).build()

            // assert
            assertThat(payment.totalPrice).isEqualTo(Price(3000L))
        }

        @DisplayName("결제 정보를 생성하면 상태는 PENDING로 초기화된다.")
        @Test
        fun initialStatusIsPending() {
            // arrange

            // act
            val payment = aPayment().build()

            // assert
            assertThat(payment.status).isEqualTo(PaymentStatusType.PENDING)
        }

        @DisplayName("결제 완료 처리 시 상태는 COMPLETED로 변경된다.")
        @Test
        fun completePaymentChangesStatusToCompleted() {
            // arrange
            val payment = aPayment().build()

            // act
            payment.complete()

            // assert
            assertThat(payment.status).isEqualTo(PaymentStatusType.COMPLETED)
        }

        @DisplayName("결제 실패 처리 시 상태는 FAILED로 변경된다.")
        @Test
        fun failPaymentChangesStatusToFailed() {
            // arrange
            val payment = aPayment().build()

            // act
            payment.fail()

            // assert
            assertThat(payment.status).isEqualTo(PaymentStatusType.FAILED)
        }

        @DisplayName("결제 취소 처리 시 상태는 CANCELED로 변경된다.")
        @Test
        fun cancelPaymentChangesStatusToCanceled() {
            // arrange
            val payment = aPayment().build()

            // act
            payment.cancel()

            // assert
            assertThat(payment.status).isEqualTo(PaymentStatusType.CANCELED)
        }
    }
}
