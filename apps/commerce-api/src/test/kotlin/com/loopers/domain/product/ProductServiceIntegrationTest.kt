package com.loopers.domain.product

import com.loopers.domain.brand.BrandEntityFixture.Companion.aBrand
import com.loopers.domain.brand.BrandService
import com.loopers.domain.vo.Price
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.math.BigDecimal
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
            // TODO: 브랜드 커맨드 생성
            val productCreateCommand = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            productService.createProduct(productCreateCommand)

            // act
            val exception = assertThrows<CoreException> {
                productService.createProduct(productCreateCommand)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.CONFLICT) },
                { assertThat(exception.message).isEqualTo("이미 존재하는 상품입니다: ${productCreateCommand.name}") },
            )
        }

        @DisplayName("브랜드가 다를 경우 상품명이 동일해도 상품을 등록할 수 있다.")
        @Test
        fun createsProduct_whenBrandIsDifferent() {
            // arrange
            // TODO: 브랜드 커맨드 생성
            val brandEntity1 = aBrand().name("브랜드A").build()
            val brandEntity2 = aBrand().name("브랜드B").build()
            val createBrand1 = brandService.createBrand(brandEntity1)
            val createBrand2 = brandService.createBrand(brandEntity2)
            val productCreateCommand1 = ProductCommand.Create(
                brandEntity1.id,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val productCreateCommand2 = ProductCommand.Create(
                brandEntity2.id,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )

            // act
            val createdProduct1 = productService.createProduct(productCreateCommand1)
            val createdProduct2 = productService.createProduct(productCreateCommand2)

            // assert
            assertAll(
                { assertThat(createdProduct1.name).isEqualTo(productCreateCommand1.name) },
                { assertThat(createdProduct2.name).isEqualTo(productCreateCommand2.name) },
                { assertThat(createdProduct1.brandId).isEqualTo(createBrand1.id) },
                { assertThat(createdProduct2.brandId).isEqualTo(createBrand2.id) },
            )
        }

        @DisplayName("상품명, 설명, 가격, 상품 상태가 유효한 경우, 상품을 등록한다.")
        @Test
        fun createsProduct_whenAllFieldsAreValid() {
            // arrange
            val productCreateCommand = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )

            // act
            val createdProduct = productService.createProduct(productCreateCommand)

            // assert
            assertAll(
                { assertThat(createdProduct.name).isEqualTo(productCreateCommand.name) },
                { assertThat(createdProduct.description).isEqualTo(productCreateCommand.description) },
                { assertThat(createdProduct.price).isEqualTo(productCreateCommand.price) },
                { assertThat(createdProduct.status).isEqualTo(productCreateCommand.status) },
            )
        }
    }

    /*
    **🔗 통합 테스트
    - [ ] 상품 목록은 페이지 번호와 페이지 크기를 기준으로 조회할 수 있다.
    - [ ] 상품 목록은 상품명으로 부분 일치 (Like) 검색할 수 있으며 대소문자를 구분하지 않는다.
    - [ ] 상품 목록은 가격 범위로 검색할 수 있다.
    - [ ] 상품명과 일치하는 상품 목록이 없을 경우 빈 목록을 반환한다.
    - [ ] 상품 목록은 가격 오름차순으로 정렬할 수 있다.
    - [ ] 상품 목록은 가격 내림차순으로 정렬할 수 있다.
    - [ ] 상품 목록은 등록일 오름차순으로 정렬할 수 있다.
    - [ ] 상품 목록은 등록일 내림차순으로 정렬할 수 있다.
    - [ ] 상품 목록은 좋아요 수 오름차순으로 정렬할 수 있다.
    - [ ] 상품 목록은 좋아요 수 내림차순으로 정렬할 수 있다.
     */
    @DisplayName("상품을 검색할 때, ")
    @Nested
    inner class Search {
        @DisplayName("상품 목록은 페이지 번호와 페이지 크기를 기준으로 조회할 수 있다.")
        @Test
        fun returnsProductListByPageAndSize() {
            // arrange
            val productCreateCommand1 = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val productCreateCommand2 = ProductCommand.Create(
                1L,
                "상품B",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            productService.createProduct(productCreateCommand1)
            productService.createProduct(productCreateCommand2)

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productService.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage).extracting("name").containsExactlyInAnyOrder("상품A", "상품B") },
            )
        }

        @DisplayName("상품 목록은 상품명으로 부분 일치 (Like) 검색할 수 있으며 대소문자를 구분하지 않는다.")
        @Test
        fun returnsProductsByPartialNameSearch() {
            // arrange
            val productCreateCommand1 = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val productCreateCommand2 = ProductCommand.Create(
                1L,
                "상품B",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            productService.createProduct(productCreateCommand1)
            productService.createProduct(productCreateCommand2)

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productService.searchProducts(ProductSearchCondition("상품"), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage).extracting("name").containsExactlyInAnyOrder("상품A", "상품B") },
            )
        }

        @DisplayName("상품 목록은 가격 범위로 검색할 수 있다.")
        @Test
        fun returnsProductsByPriceRange() {
            // arrange
            val productCreateCommand1 = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val productCreateCommand2 = ProductCommand.Create(
                1L,
                "상품B",
                Price(2000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val createdProduct1 = productService.createProduct(productCreateCommand1)
            productService.createProduct(productCreateCommand2)

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productService.searchProducts(ProductSearchCondition(minPrice = BigDecimal(500.0), maxPrice = BigDecimal(1500.0)), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(1) },
                { assertThat(productsPage.totalElements).isEqualTo(1) },
                { assertThat(productsPage.content[0].name).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[0].price).isEqualTo(createdProduct1.price) },
                { assertThat(productsPage.content[0].status).isEqualTo(createdProduct1.status) },
            )
        }

        @DisplayName("상품명과 일치하는 상품 목록이 없을 경우 빈 목록을 반환한다.")
        @Test
        fun returnsEmptyList_whenNoMatchingProducts() {
            // arrange
            val productCreateCommand1 = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val productCreateCommand2 = ProductCommand.Create(
                1L,
                "상품B",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            productService.createProduct(productCreateCommand1)
            productService.createProduct(productCreateCommand2)

            // act
            val pageRequest = PageRequest.of(0, 10)
            val products = productService.searchProducts(ProductSearchCondition("nonExistsProduct"), pageRequest)

            // assert
            assertThat(products).isEmpty()
        }

        @DisplayName("상품 목록은 가격 오름차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByPriceAsc() {
            // arrange
            val productCreateCommand1 = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val productCreateCommand2 = ProductCommand.Create(
                1L,
                "상품B",
                Price(2000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val createdProduct1 = productService.createProduct(productCreateCommand1)
            val createdProduct2 = productService.createProduct(productCreateCommand2)

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").ascending())
            val productsPage = productService.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].price).isEqualTo(createdProduct1.price) },
                { assertThat(productsPage.content[0].name).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[1].price).isEqualTo(createdProduct2.price) },
                { assertThat(productsPage.content[1].name).isEqualTo(createdProduct2.name) },
            )
        }

        @DisplayName("상품 목록은 가격 내림차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByPriceDesc() {
            // arrange
            val productCreateCommand1 = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val productCreateCommand2 = ProductCommand.Create(
                1L,
                "상품B",
                Price(2000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val createdProduct1 = productService.createProduct(productCreateCommand1)
            val createdProduct2 = productService.createProduct(productCreateCommand2)

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").descending())
            val productsPage = productService.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].price).isEqualTo(createdProduct2.price) },
                { assertThat(productsPage.content[0].name).isEqualTo(createdProduct2.name) },
                { assertThat(productsPage.content[1].price).isEqualTo(createdProduct1.price) },
                { assertThat(productsPage.content[1].name).isEqualTo(createdProduct1.name) },
            )
        }

        @DisplayName("상품 목록은 등록일 오름차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByCreatedAtAsc() {
            // arrange
            val productCreateCommand1 = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val productCreateCommand2 = ProductCommand.Create(
                1L,
                "상품B",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val createdProduct1 = productService.createProduct(productCreateCommand1)
            Thread.sleep(10)
            val createdProduct2 = productService.createProduct(productCreateCommand2)

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").ascending())
            val productsPage = productService.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].name).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[1].name).isEqualTo(createdProduct2.name) },
            )
        }

        @DisplayName("상품 목록은 등록일 내림차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByCreatedAtDesc() {
            // arrange
            val productCreateCommand1 = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val productCreateCommand2 = ProductCommand.Create(
                1L,
                "상품B",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val createdProduct1 = productService.createProduct(productCreateCommand1)
            Thread.sleep(10)
            val createdProduct2 = productService.createProduct(productCreateCommand2)

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending())
            val productsPage = productService.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].name).isEqualTo(createdProduct2.name) },
                { assertThat(productsPage.content[1].name).isEqualTo(createdProduct1.name) },
            )
        }

        @DisplayName("상품 목록은 좋아요 수 오름차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByLikesAsc() {
            // TODO: implement
        }

        @DisplayName("상품 목록은 좋아요 수 내림차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByLikesDesc() {
            // TODO: implement
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
            val productCreateProduct = ProductCommand.Create(
                1L,
                "상품A",
                Price(1000),
                "This is a test product.",
                ProductEntity.ProductStatusType.ACTIVE,
            )
            val createdProduct = productService.createProduct(productCreateProduct)

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
