package com.loopers.infrastructure.payment.gateway

import com.loopers.domain.payment.gateway.PaymentGateway
import com.loopers.domain.payment.gateway.PaymentGatewayCommand
import com.loopers.domain.payment.gateway.PaymentGatewayResult
import com.loopers.interfaces.api.ApiResponse
import com.loopers.support.enums.payment.PaymentCardType
import com.loopers.support.enums.payment.PaymentStatusType
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@SpringBootTest
class PgSimulatorGatewayTest @Autowired constructor(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val paymentGateway: PaymentGateway,
) {
    private lateinit var circuitBreaker: CircuitBreaker

    @MockitoBean
    private lateinit var pgSimulatorFeignClient: PgSimulatorFeignClient

    @BeforeEach
    fun setUp() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("pg-request")
        circuitBreaker.reset()
    }

    /*
    **🔗 서킷 브레이커 테스트
    - [ ] 정상 응답 시 서킷 브레이커는 닫힌 상태(CLOSED)를 유지한다.
    - [ ] 임계치 이상 실패 시 서킷 브레이커는 열림 상태(OPEN)가 된다. [failure-rate-threshold: 50]
    - [ ] 임계치 이상 실패해도 최소 호출 횟수 미만이면 서킷 브레이커는 열린 상태(OPEN)가 되지 않는다. [minimum-number-of-calls: 3]
    - [ ] Half-Open 상태에서 OPEN 상태로 전환하기 위한 최소 요청 수가 성공하면 서킷 브레이커는 닫힌 상태(CLOSED)가 된다. [permitted-number-of-calls-in-half-open-state: 1, max-wait-duration-in-half-open-state: 1s]
    - [ ] Half-Open 상태에서 OPEN 상태로 전환하기 위한 최소 요청 수가 실패하면 서킷 브레이커는 다시 열린 상태(OPEN)가 된다. [permitted-number-of-calls-in-half-open-state: 1, max-wait-duration-in-half-open-state: 1s]
    - [ ] 서킷 브레이커가 열림 상태(OPEN) 상태일 경우 실제 메서드가 호출되지 않고 fallback 메서드가 호출된다.
    - [ ] 서킷 브레이커가 열림 상태(OPEN) 상태일 경우 [wait-duration-in-open-state] 설정에 따라 일정 시간 후 Half-Open 상태가 된다. [wait-duration-in-open-state: 2s]
    - [ ] 임계치 이상 느린 호출이 발생할 시 서킷 브레이커는 열림 상태(OPEN)가 된다. [slow-call-rate-threshold: 50, slow-call-duration-threshold: 1s]
    - [ ] 임계치 이상 느린 호출이 발생하지 않으면 서킷 브레이커는 닫힌 상태(CLOSED)를 유지한다. [slow-call-rate-threshold: 50, slow-call-duration-threshold: 1s]
     */
    @DisplayName("결제 요청을 할 때, ")
    @Nested
    inner class RequestPayment {
        @DisplayName("정상 응답 시 서킷 브레이커는 닫힌 상태(CLOSED)를 유지한다.")
        @Test
        fun givenOkResponse_whenRequestPayment_thenCircuitBreakerStaysClosed() {
            // arrange
            val request = PaymentGatewayCommand.Request(
                "ORDER-1",
                PaymentCardType.HYUNDAI,
                "1234-5678-9012-3456",
                1000L,
                "http://localhost:8080/payments/callback",
            )
            whenever(pgSimulatorFeignClient.createPayment(any(), any()))
                .thenReturn(ApiResponse.success(PaymentGatewayResult.Requested("T-OK", PaymentStatusType.PENDING)))

            // act
            val result = paymentGateway.requestPayment(1L, request)

            // assert
            assertThat(result.transactionKey).isEqualTo("T-OK")
            assertThat(result.status).isEqualTo(PaymentStatusType.PENDING)
            verify(pgSimulatorFeignClient, times(1)).createPayment(any(), any())
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.CLOSED)
        }

        @DisplayName("임계치 이상 실패 시 서킷 브레이커는 열림 상태(OPEN)가 된다. [failure-rate-threshold: 50]")
        @Test
        fun givenFailureResponse_whenRequestPayment_thenCircuitBreakerOpens() {
            // arrange
            val request = PaymentGatewayCommand.Request(
                "ORDER-1",
                PaymentCardType.HYUNDAI,
                "1234-5678-9012-3456",
                1000L,
                "http://localhost:8080/payments/callback",
            )
            whenever(pgSimulatorFeignClient.createPayment(any(), any()))
                .thenThrow(RuntimeException("Internal Server Error"))

            // act
            repeat(circuitBreaker.circuitBreakerConfig.minimumNumberOfCalls) {
                try {
                    paymentGateway.requestPayment(1L, request)
                } catch (e: Exception) {
                    // expected exception
                }
            }

            // assert
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.OPEN)
        }

        @DisplayName("임계치 이상 실패해도 최소 호출 횟수 미만이면 서킷 브레이커는 열린 상태(OPEN)가 되지 않는다. [minimum-number-of-calls: 3]")
        @Test
        fun givenLessThanMinimumCalls_whenRequestPayment_thenCircuitBreakerStaysClosed() {
            // arrange
            val request = PaymentGatewayCommand.Request(
                "ORDER-1",
                PaymentCardType.HYUNDAI,
                "1234-5678-9012-3456",
                1000L,
                "http://localhost:8080/payments/callback",
            )
            whenever(pgSimulatorFeignClient.createPayment(any(), any()))
                .thenThrow(RuntimeException("Internal Server Error"))

            // act
            repeat(circuitBreaker.circuitBreakerConfig.minimumNumberOfCalls - 1) {
                try {
                    paymentGateway.requestPayment(1L, request)
                } catch (e: Exception) {
                    // expected exception
                }
            }

            // assert
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.CLOSED)
        }

        @DisplayName("Half-Open 상태에서 OPEN 상태로 전환하기 위한 최소 요청 수가 성공하면 서킷 브레이커는 닫힌 상태(CLOSED)가 된다. [permitted-number-of-calls-in-half-open-state: 1]")
        @Test
        fun givenHalfOpenState_whenRequestPayment_thenCircuitBreakerCloses() {
            // arrange
            circuitBreaker.transitionToOpenState()
            circuitBreaker.transitionToHalfOpenState()

            val request = PaymentGatewayCommand.Request(
                "ORDER-1",
                PaymentCardType.HYUNDAI,
                "1234-5678-9012-3456",
                1000L,
                "http://localhost:8080/payments/callback",
            )

            // assert
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.HALF_OPEN)
            val waitDurationInOpenState = 2L
            TimeUnit.SECONDS.sleep(waitDurationInOpenState + 1)

            // 요청 성공 mock
            whenever(pgSimulatorFeignClient.createPayment(any(), any()))
                .thenReturn(ApiResponse.success(PaymentGatewayResult.Requested("T-HALF-OPEN", PaymentStatusType.PENDING)))

            // act
            paymentGateway.requestPayment(1L, request)

            // assert
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.CLOSED)
        }

        @DisplayName("Half-Open 상태에서 OPEN 상태로 전환하기 위한 최소 요청 수가 실패하면 서킷 브레이커는 다시 열린 상태(OPEN)가 된다. [permitted-number-of-calls-in-half-open-state: 1]")
        @Test
        fun givenHalfOpenState_whenRequestPayment_thenCircuitBreakerOpensAgain() {
            // arrange
            circuitBreaker.transitionToOpenState()
            circuitBreaker.transitionToHalfOpenState()
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.HALF_OPEN)

            val request = PaymentGatewayCommand.Request(
                "ORDER-1",
                PaymentCardType.HYUNDAI,
                "1234-5678-9012-3456",
                1000L,
                "http://localhost:8080/payments/callback",
            )
            val waitDurationInOpenState = 2L
            TimeUnit.SECONDS.sleep(waitDurationInOpenState)

            // 요청 실패 mock
            whenever(pgSimulatorFeignClient.createPayment(any(), any()))
                .thenThrow(RuntimeException("Internal Server Error"))

            // act
            try {
                paymentGateway.requestPayment(1L, request)
            } catch (e: Exception) {
                // expected exception
            }

            // assert
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.OPEN)
        }

        @DisplayName("서킷 브레이커가 열림 상태(OPEN) 상태일 경우 실제 메서드가 호출되지 않고 fallback 메서드가 호출된다.")
        @Test
        fun givenOpenState_whenRequestPayment_thenFallbackCalled() {
            // arrange
            val request = PaymentGatewayCommand.Request(
                "ORDER-1",
                PaymentCardType.HYUNDAI,
                "1234-5678-9012-3456",
                1000L,
                "http://localhost:8080/payments/callback",
            )
            circuitBreaker.transitionToOpenState()

            // act
            val result = paymentGateway.requestPayment(1L, request)

            // assert
            assertThat(result.transactionKey).isNull()
            assertThat(result.status).isEqualTo(PaymentStatusType.FAILED)
            verify(pgSimulatorFeignClient, times(0)).createPayment(any(), any())
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.OPEN)
        }

        @DisplayName("서킷 브레이커가 열림 상태(OPEN) 상태일 경우 [wait-duration-in-open-state] 설정에 따라 일정 시간 후 Half-Open 상태가 된다. [wait-duration-in-open-state: 2s]")
        @Test
        fun givenOpenState_whenWaitDuration_thenCircuitBreakerResetsAfterTimeout() {
            // arrange
            circuitBreaker.transitionToOpenState()
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.OPEN)

            // 요청 성공 mock
            whenever(pgSimulatorFeignClient.createPayment(any(), any()))
                .thenReturn(ApiResponse.success(PaymentGatewayResult.Requested("T-HALF-OPEN", PaymentStatusType.PENDING)))

            val waitDurationInOpenState = 2L
            TimeUnit.SECONDS.sleep(waitDurationInOpenState + 1)

            // act
            val seenState = AtomicReference<CircuitBreaker.State>()
            circuitBreaker.decorateSupplier {
                // Half-Open으로 진입한 직후
                seenState.set(circuitBreaker.state)
            }.get()

            // assert
            assertThat(seenState.get()).isEqualTo(CircuitBreaker.State.HALF_OPEN)
            // 최종 상태는 CLOSED
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.CLOSED)
        }

        @DisplayName("임계치 이상 느린 호출이 발생할 시 서킷 브레이커는 열림 상태(OPEN)가 된다. [slow-call-rate-threshold: 50, slow-call-duration-threshold: 1s]")
        @Test
        fun givenSlowCall_whenRequestPayment_thenCircuitBreakerOpens() {
            // arrange
            val request = PaymentGatewayCommand.Request(
                "ORDER-1",
                PaymentCardType.HYUNDAI,
                "1234-5678-9012-3456",
                1000L,
                "http://localhost:8080/payments/callback",
            )
            whenever(pgSimulatorFeignClient.createPayment(any(), any()))
                .thenAnswer {
                    TimeUnit.SECONDS.sleep(circuitBreaker.circuitBreakerConfig.slowCallDurationThreshold.plusSeconds(1).toSeconds())
                    ApiResponse.success(PaymentGatewayResult.Requested("T-SLOW", PaymentStatusType.PENDING))
                }

            // act
            repeat(circuitBreaker.circuitBreakerConfig.minimumNumberOfCalls) {
                try {
                    paymentGateway.requestPayment(1L, request)
                } catch (e: Exception) {
                    // expected exception
                }
            }

            // assert
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.OPEN)
        }

        @DisplayName("임계치 이상 느린 호출이 발생하지 않으면 서킷 브레이커는 닫힌 상태(CLOSED)를 유지한다. [slow-call-rate-threshold: 50, slow-call-duration-threshold: 1s]")
        @Test
        fun givenFastCall_whenRequestPayment_thenCircuitBreakerStaysClosed() {
            // arrange
            val request = PaymentGatewayCommand.Request(
                "ORDER-1",
                PaymentCardType.HYUNDAI,
                "1234-5678-9012-3456",
                1000L,
                "http://localhost:8080/payments/callback",
            )
            whenever(pgSimulatorFeignClient.createPayment(any(), any()))
                .thenReturn(ApiResponse.success(PaymentGatewayResult.Requested("T-FAST", PaymentStatusType.PENDING)))

            // act
            repeat(circuitBreaker.circuitBreakerConfig.minimumNumberOfCalls) {
                paymentGateway.requestPayment(1L, request)
            }

            // assert
            assertThat(circuitBreaker.state).isEqualTo(CircuitBreaker.State.CLOSED)
        }
    }
}
