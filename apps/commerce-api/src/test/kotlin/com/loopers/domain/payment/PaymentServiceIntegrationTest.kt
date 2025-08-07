package com.loopers.domain.payment

import com.loopers.domain.vo.Price
import com.loopers.support.enums.payment.PaymentMethodType
import com.loopers.support.enums.payment.PaymentStatusType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PaymentServiceIntegrationTest @Autowired constructor(
    private val paymentService: PaymentService,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
     **🔗 통합 테스트**
    - [ ] 결제가 생성되면 결제 상태가 PENDING이다.
    - [ ] 결제가 완료되면 결제 상태가 COMPLETED로 변경된다.
    - [ ] 결제가 실패하면 결제 상태가 FAILED로 변경된다.
    - [ ] 결제가 취소되면 결제 상태가 CANCELED로 변경된다.
    - [ ] 결제가 생성되면 결제 총 금액을 계산한다.
     */
    @DisplayName("결제를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("결제가 생성되면 결제 상태가 PENDING이다.")
        @Test
        fun paymentStatusIsPending_whenPaymentCreated() {
            // arrange
            val command = PaymentCommand.Create(
                1L,
                PaymentMethodType.POINT,
                Price(3000),
            )

            // act
            val paymentEntity = paymentService.createPayment(command)

            // assert
            assertThat(paymentEntity.status).isEqualTo(PaymentStatusType.PENDING)
        }

        @DisplayName("결제가 완료되면 결제, 결제 항목의 상태가 COMPLETED로 변경된다.")
        @Test
        fun paymentStatusIsCompleted_whenPaymentIsSuccessful() {
            // arrange
            val command = PaymentCommand.Create(
                1L,
                PaymentMethodType.POINT,
                Price(3000),
            )

            // act
            val paymentEntity = paymentService.createPayment(command)
            paymentEntity.complete()

            // assert
            assertThat(paymentEntity.status).isEqualTo(PaymentStatusType.COMPLETED)
        }

        @DisplayName("결제가 실패하면 결제, 결제 항목의 상태가 FAILED로 변경된다.")
        @Test
        fun paymentStatusIsFailed_whenPaymentFails() {
            // arrange
            val command = PaymentCommand.Create(
                1L,
                PaymentMethodType.POINT,
                Price(3000),
            )

            // act
            val paymentEntity = paymentService.createPayment(command)
            paymentEntity.fail()

            // assert
            assertThat(paymentEntity.status).isEqualTo(PaymentStatusType.FAILED)
        }

        @DisplayName("결제가 취소되면 결제, 결제 항목의 상태가 CANCELED로 변경된다.")
        @Test
        fun paymentStatusIsCanceled_whenPaymentIsCanceled() {
            // arrange
            val command = PaymentCommand.Create(
                1L,
                PaymentMethodType.POINT,
                Price(3000),
            )

            // act
            val paymentEntity = paymentService.createPayment(command)
            paymentEntity.cancel()

            // assert
            assertThat(paymentEntity.status).isEqualTo(PaymentStatusType.CANCELED)
        }

        @DisplayName("결제가 생성되면 결제 총 금액을 계산한다.")
        @Test
        fun calculatesTotalAmount_whenPaymentIsCreated() {
            // arrange
            val command = PaymentCommand.Create(
                1L,
                PaymentMethodType.POINT,
                Price(3000),
            )

            // act
            val paymentEntity = paymentService.createPayment(command)

            // assert
            assertThat(paymentEntity.totalPrice).isEqualTo(Price(3000))
        }
    }
}
