package com.loopers.domain.payment

import com.loopers.domain.vo.Price
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
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
    - [ ] 결제가 생성되면 결제 총 금액을 계산한다.
     */
    @DisplayName("결제를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("결제가 생성되면 결제, 결제 항목의 상태가 PENDING이다.")
        @Test
        fun paymentStatusIsPending_whenPaymentCreated() {
            // arrange
            val command = PaymentCommand.Create(
                1L,
                PaymentEntity.PaymentMethodType.POINT,
                listOf(
                    PaymentCommand.Create.PaymentItemCommand(
                        1L,
                        Price(10_000),
                    ),
                    PaymentCommand.Create.PaymentItemCommand(
                        2L,
                        Price(20_000),
                    ),
                ),
            )

            // act
            val paymentEntity = paymentService.createPayment(command)

            // assert
            assertAll(
                { assertThat(paymentEntity.status).isEqualTo(PaymentEntity.PaymentStatusType.PENDING) },
                { assertThat(paymentEntity.paymentItems.isAllPending()).isTrue() },
            )
        }

        @DisplayName("결제가 완료되면 결제, 결제 항목의 상태가 COMPLETED로 변경된다.")
        @Test
        fun paymentStatusIsCompleted_whenPaymentIsSuccessful() {
            // arrange
            val command = PaymentCommand.Create(
                1L,
                PaymentEntity.PaymentMethodType.POINT,
                listOf(
                    PaymentCommand.Create.PaymentItemCommand(
                        1L,
                        Price(10_000),
                    ),
                    PaymentCommand.Create.PaymentItemCommand(
                        2L,
                        Price(20_000),
                    ),
                ),
            )

            // act
            val paymentEntity = paymentService.createPayment(command)
            paymentEntity.complete()

            // assert
            assertAll(
                { assertThat(paymentEntity.status).isEqualTo(PaymentEntity.PaymentStatusType.COMPLETED) },
                { assertThat(paymentEntity.paymentItems.isAllCompleted()).isTrue() },
            )
        }

        @DisplayName("결제가 실패하면 결제, 결제 항목의 상태가 FAILED로 변경된다.")
        @Test
        fun paymentStatusIsFailed_whenPaymentFails() {
            // arrange
            val command = PaymentCommand.Create(
                1L,
                PaymentEntity.PaymentMethodType.POINT,
                listOf(
                    PaymentCommand.Create.PaymentItemCommand(
                        1L,
                        Price(10_000),
                    ),
                    PaymentCommand.Create.PaymentItemCommand(
                        2L,
                        Price(20_000),
                    ),
                ),
            )

            // act
            val paymentEntity = paymentService.createPayment(command)
            paymentEntity.fail()

            // assert
            assertAll(
                { assertThat(paymentEntity.status).isEqualTo(PaymentEntity.PaymentStatusType.FAILED) },
                { assertThat(paymentEntity.paymentItems.isAllFailed()).isTrue() },
            )
        }

        @DisplayName("결제가 생성되면 결제 총 금액을 계산한다.")
        @Test
        fun calculatesTotalAmount_whenPaymentIsCreated() {
            // arrange
            val command = PaymentCommand.Create(
                1L,
                PaymentEntity.PaymentMethodType.POINT,
                listOf(
                    PaymentCommand.Create.PaymentItemCommand(
                        1L,
                        Price(10_000),
                    ),
                    PaymentCommand.Create.PaymentItemCommand(
                        2L,
                        Price(20_000),
                    ),
                ),
            )

            // act
            val paymentEntity = paymentService.createPayment(command)

            // assert
            assertAll(
                { assertThat(paymentEntity.totalAmount).isEqualTo(Price(30_000)) },
                { assertThat(paymentEntity.paymentItems.totalAmount()).isEqualTo(Price(30_000)) },
            )
        }

    }


}
