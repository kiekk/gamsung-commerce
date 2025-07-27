package com.loopers.domain.product

import com.loopers.domain.brand.BrandEntityFixture.Companion.aBrand
import com.loopers.domain.brand.BrandService
import com.loopers.domain.product.ProductEntityFixture.Companion.aProduct
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class ProductServiceIntegrationTest @Autowired constructor(
    private val productService: ProductService,
    private val brandService: BrandService,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
    **🔗 통합 테스트
    - [ ] 같은 브랜드 내에서 상품명이 중복될 경우, 상품 등록에 실패한다.
    - [ ] 브랜드가 다를 경우 상품명이 동일해도 상품을 등록할 수 있다.
    - [ ] 상품명, 설명, 가격, 상품 상태가 유효한 경우, 상품을 등록한다.
     */
    @DisplayName("상품을 등록할 때, ")
    @Nested
    inner class Create {
        @DisplayName("같은 브랜드 내에서 상품명이 중복될 경우, 상품 등록에 실패한다.")
        @Test
        fun failsToCreateProduct_whenNameIsDuplicate() {
            // arrange
            val brandEntity = aBrand().build()
            val createBrand = brandService.createBrand(brandEntity)
            val productEntity = aProduct().brandId(createBrand.id).name("상품A").build()
            productService.createProduct(productEntity)

            // act
            val exception = assertThrows<CoreException> {
                productService.createProduct(productEntity)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.CONFLICT) },
                { assertThat(exception.message).isEqualTo("이미 존재하는 상품입니다: ${productEntity.name}") },
            )
        }

        @DisplayName("브랜드가 다를 경우 상품명이 동일해도 상품을 등록할 수 있다.")
        @Test
        fun createsProduct_whenBrandIsDifferent() {
            // arrange
            val brandEntity1 = aBrand().name("브랜드A").build()
            val brandEntity2 = aBrand().name("브랜드B").build()
            val createBrand1 = brandService.createBrand(brandEntity1)
            val createBrand2 = brandService.createBrand(brandEntity2)
            val productEntity1 = aProduct().brandId(createBrand1.id).name("상품A").build()
            val productEntity2 = aProduct().brandId(createBrand2.id).name("상품A").build()

            // act
            val createdProduct1 = productService.createProduct(productEntity1)
            val createdProduct2 = productService.createProduct(productEntity2)

            // assert
            assertAll(
                { assertThat(createdProduct1.name).isEqualTo(productEntity1.name) },
                { assertThat(createdProduct2.name).isEqualTo(productEntity2.name) },
                { assertThat(createdProduct1.brandId).isEqualTo(createBrand1.id) },
                { assertThat(createdProduct2.brandId).isEqualTo(createBrand2.id) },
            )
        }

        @DisplayName("상품명, 설명, 가격, 상품 상태가 유효한 경우, 상품을 등록한다.")
        @Test
        fun createsProduct_whenAllFieldsAreValid() {
            // arrange
            val productEntity = aProduct().build()

            // act
            val createdProduct = productService.createProduct(productEntity)

            // assert
            assertAll(
                { assertThat(createdProduct.name).isEqualTo(productEntity.name) },
                { assertThat(createdProduct.description).isEqualTo(productEntity.description) },
                { assertThat(createdProduct.price).isEqualTo(productEntity.price) },
                { assertThat(createdProduct.status).isEqualTo(productEntity.status) },
            )
        }
    }

    /*
    **🔗 통합 테스트
    - [ ] 상품 ID에 해당하는 상품이 존재하지 않을 경우, null을 반환한다.
    - [ ] 상품 ID에 해당하는 상품이 존재할 경우, 해당 상품을 반환한다.
     */
    @DisplayName("상품을 조회할 때, ")
    @Nested
    inner class Get {
        @DisplayName("상품 ID에 해당하는 상품이 존재하지 않을 경우, null을 반환한다.")
        @Test
        fun returnsNull_whenProductDoesNotExist() {
            // arrange
            val nonExistentProductId = 999L

            // act
            val product = productService.getProduct(nonExistentProductId)

            // assert
            assertThat(product).isNull()
        }

        @DisplayName("상품 ID에 해당하는 상품이 존재할 경우, 해당 상품을 반환한다.")
        @Test
        fun returnsProduct_whenProductExists() {
            // arrange
            val productEntity = aProduct().build()
            val createdProduct = productService.createProduct(productEntity)

            // act
            val product = productService.getProduct(createdProduct.id)

            // assert
            assertAll(
                { assertThat(product?.id).isEqualTo(createdProduct.id) },
                { assertThat(product?.name).isEqualTo(createdProduct.name) },
                { assertThat(product?.description).isEqualTo(createdProduct.description) },
                { assertThat(product?.price).isEqualTo(createdProduct.price) },
                { assertThat(product?.status).isEqualTo(createdProduct.status) },
            )
        }
    }
}
