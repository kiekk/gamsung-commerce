package com.loopers.domain.product

import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.infrastructure.product.ProductJpaRepository
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
class ProductValidationServiceTest @Autowired constructor(
    private val productValidationService: ProductValidationService,
    private val productJpaRepository: ProductJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /* 🔗 통합 테스트
     - [ ] 존재하지 않는 상품 ID로 유효성 검사를 시도하면 예외가 발생한다.
     - [ ] 비활성 상태의 상품 ID로 유효성 검사를 시도하면 예외가 발생한다.
     - [ ] 활성 상태의 상품 ID로 유효성 검사를 시도하면 예외가 발생하지 않는다.
     */
    @DisplayName("상품 유효성 검사 테스트, ")
    @Nested
    inner class Validate {
        @DisplayName("존재하지 않는 상품 ID로 유효성 검사를 시도하면 예외가 발생한다.")
        @Test
        fun throwsException_whenProductIdDoesNotExist() {
            // arrange
            val nonExistProductId = 999L

            // act
            val exception = assertThrows<CoreException> {
                productValidationService.validate(nonExistProductId)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.NOT_FOUND) },
                { assertThat(exception.message).isEqualTo("존재하지 않는 상품입니다. productId: $nonExistProductId") },
            )
        }

        @DisplayName("비활성 상태의 상품 ID로 유효성 검사를 시도하면 예외가 발생한다.")
        @Test
        fun throwsException_whenProductIsInactive() {
            // arrange
            val inactiveProduct = productJpaRepository.save(aProduct().build().apply { inactive() })

            // act
            val exception = assertThrows<CoreException> {
                productValidationService.validate(inactiveProduct.id)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.CONFLICT) },
                { assertThat(exception.message).isEqualTo("주문 가능한 상태가 아닌 상품입니다. productId: ${inactiveProduct.id}, 상태: ${inactiveProduct.status}") },
            )
        }

        @DisplayName("활성 상태의 상품 ID로 유효성 검사를 시도하면 예외가 발생하지 않는다.")
        @Test
        fun doesNotThrowException_whenProductIsActive() {
            // arrange
            val activeProduct = productJpaRepository.save(aProduct().build())

            // act & assert
            assertDoesNotThrow {
                productValidationService.validate(activeProduct.id)
            }
        }
    }
}
