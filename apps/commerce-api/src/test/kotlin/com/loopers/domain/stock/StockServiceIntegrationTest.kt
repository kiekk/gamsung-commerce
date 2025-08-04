package com.loopers.domain.stock

import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class StockServiceIntegrationTest @Autowired constructor(
    private val stockService: StockService,
    private val productJpaRepository: ProductJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
     **🔗 통합 테스트
    - [ ] 유효한 상품 ID와 재고 수량이 주어지면 재고 등록에 성공한다.
     */
    @DisplayName("재고를 등록할 때, ")
    @Nested
    inner class Create {
        @DisplayName("상품 ID와 재고 수량이 주어지면 재고 등록에 성공한다.")
        @Test
        fun createsStock_whenProductExistsAndQuantityIsValid() {
            // arrange
            val createdProduct = productJpaRepository.save(aProduct().build())
            val stockCreateCommand = StockCommand.Create(
                createdProduct.id,
                20,
            )

            // act
            val createdStock = stockService.createStock(stockCreateCommand)

            // assert
            assertThat(createdStock.productId).isEqualTo(stockCreateCommand.productId)
            assertThat(createdStock.quantity).isEqualTo(stockCreateCommand.quantity)
        }
    }
}
