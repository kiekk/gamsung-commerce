package com.loopers.domain.product

import com.loopers.application.product.ProductCommand
import com.loopers.application.product.ProductFacade
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
class ProductFacadeIntegrationTest @Autowired constructor(
    private val productFacade: ProductFacade,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
    **🔗 통합 테스트

    - [ ] 상품 등록 시 재고 수량을 입력하면 재고 수량은 입력한 수량으로 설정된다.
    - [ ] 상품 등록 시 재고 수량을 입력하지 않으면 재고 수량은 0으로 설정된다.
     */
    @DisplayName("상품을 등록할 때, ")
    @Nested
    inner class Create {
        @DisplayName("상품 등록 시 재고 수량을 입력하면 재고 수량은 입력한 수량으로 설정된다.")
        @Test
        fun createsProductWithStock_whenProductCommandIsValid() {
            // arrange
            val command = ProductCommand.Create(
                1L,
                "상품A",
                Price(100L),
                "상품 설명",
                ProductEntity.ProductStatusType.ACTIVE,
                10,
            )

            // act
            val productInfo = productFacade.createProduct(command)

            // assert
            assertAll(
                { assertThat(productInfo.brandId).isEqualTo(command.brandId) },
                { assertThat(productInfo.name).isEqualTo(command.name) },
                { assertThat(productInfo.price).isEqualTo(command.price) },
                { assertThat(productInfo.description).isEqualTo(command.description) },
                { assertThat(productInfo.status).isEqualTo(command.status) },
                { assertThat(productInfo.stockQuantity).isEqualTo(command.quantity) },
            )
        }

        @DisplayName("상품 등록 시 재고 수량을 입력하지 않으면 재고 수량은 0으로 설정된다.")
        @Test
        fun createsProductWithZeroStock_whenStockQuantityIsNotProvided() {
            // arrange
            val command = ProductCommand.Create(
                1L,
                "상품A",
                Price(100L),
                "상품 설명",
                ProductEntity.ProductStatusType.ACTIVE,
            )

            // act
            val productInfo = productFacade.createProduct(command)

            // assert
            assertAll(
                { assertThat(productInfo.brandId).isEqualTo(command.brandId) },
                { assertThat(productInfo.name).isEqualTo(command.name) },
                { assertThat(productInfo.price).isEqualTo(command.price) },
                { assertThat(productInfo.description).isEqualTo(command.description) },
                { assertThat(productInfo.status).isEqualTo(command.status) },
                { assertThat(productInfo.stockQuantity).isEqualTo(0) }, // 재고 수량이 0으로 설정됨
            )
        }

    }
}
