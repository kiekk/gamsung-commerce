package com.loopers.domain.product

import com.loopers.application.product.ProductCriteria
import com.loopers.application.product.ProductFacade
import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.product.query.ProductSearchCondition
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.math.BigDecimal

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
                { assertThat(productInfo.stockQuantity).isEqualTo(0) },
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
    @DisplayName("상품 목록을 검색할 때, ")
    @Nested
    inner class Search {
        @DisplayName("상품 목록은 페이지 번호와 페이지 크기를 기준으로 조회할 수 있다.")
        @Test
        fun returnsProductListByPageAndSize() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage).extracting("productName").containsExactlyInAnyOrder("상품A", "상품B") },
            )
        }

        @DisplayName("상품 목록은 상품명으로 부분 일치 (Like) 검색할 수 있으며 대소문자를 구분하지 않는다.")
        @Test
        fun returnsProductsByPartialNameSearch() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productFacade.searchProducts(ProductSearchCondition("상품"), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage).extracting("productName").containsExactlyInAnyOrder("상품A", "상품B") },
            )
        }

        @DisplayName("상품 목록은 가격 범위로 검색할 수 있다.")
        @Test
        fun returnsProductsByPriceRange() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").price(Price(1000)).build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").price(Price(2000)).build())

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productFacade.searchProducts(ProductSearchCondition(minPrice = BigDecimal(500.0), maxPrice = BigDecimal(1500.0)), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(1) },
                { assertThat(productsPage.totalElements).isEqualTo(1) },
                { assertThat(productsPage.content[0].productName).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[0].productPrice).isEqualTo(createdProduct1.price.value) },
                { assertThat(productsPage.content[0].productStatus).isEqualTo(createdProduct1.status) },
            )
        }

        @DisplayName("상품명과 일치하는 상품 목록이 없을 경우 빈 목록을 반환한다.")
        @Test
        fun returnsEmptyList_whenNoMatchingProducts() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10)
            val products = productFacade.searchProducts(ProductSearchCondition("nonExistsProduct"), pageRequest)

            // assert
            assertThat(products).isEmpty()
        }

        @DisplayName("상품 목록은 가격 오름차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByPriceAsc() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").price(Price(1000)).build())
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").price(Price(2000)).build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").ascending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].productPrice).isEqualTo(createdProduct1.price.value) },
                { assertThat(productsPage.content[0].productName).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[1].productPrice).isEqualTo(createdProduct2.price.value) },
                { assertThat(productsPage.content[1].productName).isEqualTo(createdProduct2.name) },
            )
        }

        @DisplayName("상품 목록은 가격 내림차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByPriceDesc() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").price(Price(1000)).build())
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").price(Price(2000)).build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").descending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].productPrice).isEqualTo(createdProduct2.price.value) },
                { assertThat(productsPage.content[0].productName).isEqualTo(createdProduct2.name) },
                { assertThat(productsPage.content[1].productPrice).isEqualTo(createdProduct1.price.value) },
                { assertThat(productsPage.content[1].productName).isEqualTo(createdProduct1.name) },
            )
        }

        @DisplayName("상품 목록은 등록일 오름차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByCreatedAtAsc() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            Thread.sleep(10)
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").ascending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].productName).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[1].productName).isEqualTo(createdProduct2.name) },
            )
        }

        @DisplayName("상품 목록은 등록일 내림차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByCreatedAtDesc() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            Thread.sleep(10)
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].productName).isEqualTo(createdProduct2.name) },
                { assertThat(productsPage.content[1].productName).isEqualTo(createdProduct1.name) },
            )
        }

        @DisplayName("상품 목록은 좋아요 수 오름차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByLikesAsc() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())
            val createdProductLikeCount1 = productLikeCountRepository.save(aProductLikeCount().productId(createdProduct1.id).productLikeCount(10).build())
            val createdProductLikeCount2 = productLikeCountRepository.save(aProductLikeCount().productId(createdProduct2.id).productLikeCount(20).build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("likeCount").ascending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].productName).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[0].productLikeCount).isEqualTo(createdProductLikeCount1.productLikeCount) },
                { assertThat(productsPage.content[1].productName).isEqualTo(createdProduct2.name) },
                { assertThat(productsPage.content[1].productLikeCount).isEqualTo(createdProductLikeCount2.productLikeCount) },
            )
        }

        @DisplayName("상품 목록은 좋아요 수 내림차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByLikesDesc() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())
            val createdProductLikeCount1 = productLikeCountRepository.save(aProductLikeCount().productId(createdProduct1.id).productLikeCount(10).build())
            val createdProductLikeCount2 = productLikeCountRepository.save(aProductLikeCount().productId(createdProduct2.id).productLikeCount(20).build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("likeCount").descending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].productName).isEqualTo(createdProduct2.name) },
                { assertThat(productsPage.content[0].productLikeCount).isEqualTo(createdProductLikeCount2.productLikeCount) },
                { assertThat(productsPage.content[1].productName).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[1].productLikeCount).isEqualTo(createdProductLikeCount1.productLikeCount) },
            )
        }
    }
}
