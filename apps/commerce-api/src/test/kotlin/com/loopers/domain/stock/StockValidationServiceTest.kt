package com.loopers.domain.stock

import com.loopers.domain.stock.fixture.StockEntityFixture.Companion.aStock
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class StockValidationServiceTest @Autowired constructor(
    private val stockValidationService: StockValidationService,
    private val stockRepository: StockRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
    **🔗 통합 테스트
    - [ ] 존재하지 않는 상품 ID로 유효성 검사를 시도하면 예외가 발생한다.
    - [ ] 재고가 부족한 상품 ID로 유효성 검사를 시도하면 예외가 발생한다.
    - [ ] 재고가 충분한 상품 ID로 유효성 검사를 시도하면 예외가 발생하지 않는다.
     */
    @DisplayName("재고 유효성 검사 테스트, ")
    @Nested
    inner class Validate {
        @DisplayName("존재하지 않는 상품 ID로 유효성 검사를 시도하면 예외가 발생한다.")
        @Test
        fun throwsException_whenProductIdDoesNotExist() {
            // arrange
            val nonExistProductId = 999L
            val quantity = 1

            // act
            val exception = assertThrows<CoreException> {
                stockValidationService.validate(nonExistProductId, quantity)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND) },
                { assertThat(exception.message).isEqualTo("재고를 찾을 수 업습니다. productId: $nonExistProductId") },
            )
        }

        @DisplayName("재고가 부족한 상품 ID로 유효성 검사를 시도하면 예외가 발생한다.")
        @Test
        fun throwsException_whenStockIsInsufficient() {
            // arrange
            val productId = 1L
            val quantity = 1
            stockRepository.save(aStock().productId(productId).quantity(0).build())

            // act
            val exception = assertThrows<CoreException> {
                stockValidationService.validate(productId, quantity)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.CONFLICT) },
                { assertThat(exception.message).contains("재고가 부족한 상품입니다.") },
            )
        }

        @DisplayName("재고가 충분한 상품 ID로 유효성 검사를 시도하면 예외가 발생하지 않는다.")
        @Test
        fun doesNotThrowException_whenStockIsSufficient() {
            // arrange
            val productId = 1L
            val sufficientQuantity = 5
            stockRepository.save(aStock().productId(productId).quantity(sufficientQuantity).build())

            // act & assert
            assertDoesNotThrow {
                stockValidationService.validate(productId, sufficientQuantity)
            }
        }
    }

}
