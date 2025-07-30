package com.loopers.domain.product

import com.loopers.application.product.ProductCriteria
import com.loopers.application.product.ProductFacade
import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.productlike.ProductLikeCountRepository
import com.loopers.domain.productlike.fixture.ProductLikeCountEntityFixture.Companion.aProductLikeCount
import com.loopers.domain.vo.Price
import com.loopers.support.error.CoreException
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ProductFacadeIntegrationTest @Autowired constructor(
    private val productFacade: ProductFacade,
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
    private val productLikeCountRepository: ProductLikeCountRepository,
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
            val productCreateCriteria = ProductCriteria.Create(
                1L,
                "상품A",
                Price(100L),
                "상품 설명",
                ProductEntity.ProductStatusType.ACTIVE,
                10,
            )

            // act
            val productInfo = productFacade.createProduct(productCreateCriteria)

            // assert
            assertAll(
                { assertThat(productInfo.brandId).isEqualTo(productCreateCriteria.brandId) },
                { assertThat(productInfo.name).isEqualTo(productCreateCriteria.name) },
                { assertThat(productInfo.price).isEqualTo(productCreateCriteria.price) },
                { assertThat(productInfo.description).isEqualTo(productCreateCriteria.description) },
                { assertThat(productInfo.status).isEqualTo(productCreateCriteria.status) },
                { assertThat(productInfo.stockQuantity).isEqualTo(productCreateCriteria.quantity) },
            )
        }

        @DisplayName("상품 등록 시 재고 수량을 입력하지 않으면 재고 수량은 0으로 설정된다.")
        @Test
        fun createsProductWithZeroStock_whenStockQuantityIsNotProvided() {
            // arrange
            val productCreateCriteria = ProductCriteria.Create(
                1L,
                "상품A",
                Price(100L),
                "상품 설명",
                ProductEntity.ProductStatusType.ACTIVE,
            )

            // act
            val productInfo = productFacade.createProduct(productCreateCriteria)

            // assert
            assertAll(
                { assertThat(productInfo.brandId).isEqualTo(productCreateCriteria.brandId) },
                { assertThat(productInfo.name).isEqualTo(productCreateCriteria.name) },
                { assertThat(productInfo.price).isEqualTo(productCreateCriteria.price) },
                { assertThat(productInfo.description).isEqualTo(productCreateCriteria.description) },
                { assertThat(productInfo.status).isEqualTo(productCreateCriteria.status) },
                { assertThat(productInfo.stockQuantity).isEqualTo(0) }, // 재고 수량이 0으로 설정됨
            )
        }

    }

    /*
    **🔗 통합 테스트**
    - [ ] 상품 조회 시 상품 정보가 없으면 예외가 발생한다.
    - [ ] 상품 조회 시 브랜드 정보가 없으면 예외가 발생한다.
    - [ ] 상품은 상품 ID, 상품명, 브랜드명, 상품 가격, 상품 상태, 상품 좋아요 수 정보가 포함된다.
     */
    @DisplayName("상품을 조회할 때, ")
    @Nested
    inner class Get {
        @DisplayName("상품 조회 시 상품 정보가 없으면 예외가 발생한다.")
        @Test
        fun throwsExceptionWhenProductNotFound() {
            // arrange
            val nonExistentProductId = 999L

            // act
            val exception = assertThrows<CoreException> {
                productFacade.getProduct(nonExistentProductId)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).contains("상품을 찾을 수 없습니다. $nonExistentProductId") },
            )
        }

        @DisplayName("상품 조회 시 브랜드 정보가 없으면 예외가 발생한다.")
        @Test
        fun throwsExceptionWhenBrandNotFound() {
            // arrange
            val createdProduct = productRepository.createProduct(aProduct().build())

            // act
            val exception = assertThrows<CoreException> {
                productFacade.getProduct(createdProduct.id)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).contains("브랜드를 찾을 수 없습니다. ${createdProduct.brandId}") },
            )
        }

        @DisplayName("상품은 상품 ID, 상품명, 브랜드명, 상품 가격, 상품 상태, 상품 좋아요 수 정보가 포함된다.")
        @Test
        fun returnsProductWithBrandAndLikes() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct = productRepository.createProduct(aProduct().brandId(createdBrand.id).build())
            val createdProductLikeCount = productLikeCountRepository.save(aProductLikeCount().productId(createdProduct.id).productLikeCount(20).build())

            // act
            val productInfo = productFacade.getProduct(createdProduct.id)

            // assert
            assertAll(
                { assertThat(productInfo.id).isEqualTo(createdProduct.id) },
                { assertThat(productInfo.productName).isEqualTo(createdProduct.name) },
                { assertThat(productInfo.brandName).isEqualTo(createdBrand.name) },
                { assertThat(productInfo.productPrice).isEqualTo(createdProduct.price) },
                { assertThat(productInfo.productStatus).isEqualTo(createdProduct.status) },
                { assertThat(productInfo.productLikeCount).isEqualTo(createdProductLikeCount.productLikeCount) },
            )
        }
    }
}
