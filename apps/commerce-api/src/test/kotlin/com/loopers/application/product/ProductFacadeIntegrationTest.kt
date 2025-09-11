package com.loopers.application.product

import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.product.query.ProductSearchCondition
import com.loopers.domain.productlike.fixture.ProductLikeCountEntityFixture
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.domain.vo.Price
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.productlike.ProductLikeCountJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.KafkaMockConfig
import com.loopers.support.enums.product.ProductStatusType
import com.loopers.support.error.CoreException
import com.loopers.utils.DatabaseCleanUp
import com.loopers.utils.RedisCleanUp
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.math.BigDecimal

@Import(KafkaMockConfig::class)
@SpringBootTest
class ProductFacadeIntegrationTest @Autowired constructor(
    private val productFacade: ProductFacade,
    private val productJpaRepository: ProductJpaRepository,
    private val brandJpaRepository: BrandJpaRepository,
    private val productLikeCountJpaRepository: ProductLikeCountJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
    private val redisCleanUp: RedisCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
        redisCleanUp.truncateAll()
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
            val createdUser = userJpaRepository.save(aUser().build())
            val productCreateCriteria = ProductCriteria.Create(
                createdUser.username,
                1L,
                "상품A",
                Price(100L),
                "상품 설명",
                10,
            )

            // act
            val productInfo = productFacade.createProduct(productCreateCriteria)

            // assert
            assertAll(
                { Assertions.assertThat(productInfo.brandId).isEqualTo(productCreateCriteria.brandId) },
                { Assertions.assertThat(productInfo.name).isEqualTo(productCreateCriteria.name) },
                { Assertions.assertThat(productInfo.price).isEqualTo(productCreateCriteria.price.value) },
                { Assertions.assertThat(productInfo.description).isEqualTo(productCreateCriteria.description) },
                { Assertions.assertThat(productInfo.status).isEqualTo(ProductStatusType.ACTIVE) },
                { Assertions.assertThat(productInfo.stockQuantity).isEqualTo(productCreateCriteria.quantity) },
            )
        }

        @DisplayName("상품 등록 시 재고 수량을 입력하지 않으면 재고 수량은 0으로 설정된다.")
        @Test
        fun createsProductWithZeroStock_whenStockQuantityIsNotProvided() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val productCreateCriteria = ProductCriteria.Create(
                createdUser.username,
                1L,
                "상품A",
                Price(100L),
                "상품 설명",
            )

            // act
            val productInfo = productFacade.createProduct(productCreateCriteria)

            // assert
            assertAll(
                { Assertions.assertThat(productInfo.brandId).isEqualTo(productCreateCriteria.brandId) },
                { Assertions.assertThat(productInfo.name).isEqualTo(productCreateCriteria.name) },
                { Assertions.assertThat(productInfo.price).isEqualTo(productCreateCriteria.price.value) },
                { Assertions.assertThat(productInfo.description).isEqualTo(productCreateCriteria.description) },
                { Assertions.assertThat(productInfo.status).isEqualTo(ProductStatusType.ACTIVE) },
                { Assertions.assertThat(productInfo.stockQuantity).isEqualTo(0) },
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
                { Assertions.assertThat(exception).isInstanceOf(CoreException::class.java) },
                { Assertions.assertThat(exception.message).contains("상품을 찾을 수 없습니다. $nonExistentProductId") },
            )
        }

        @DisplayName("상품 조회 시 브랜드 정보가 없으면 예외가 발생한다.")
        @Test
        fun throwsExceptionWhenBrandNotFound() {
            // arrange
            val createdProduct = productJpaRepository.save(aProduct().build())

            // act
            val exception = assertThrows<CoreException> {
                productFacade.getProduct(createdProduct.id)
            }

            // assert
            assertAll(
                { Assertions.assertThat(exception).isInstanceOf(CoreException::class.java) },
                { Assertions.assertThat(exception.message).contains("브랜드를 찾을 수 없습니다. ${createdProduct.brandId}") },
            )
        }

        @DisplayName("상품은 상품 ID, 상품명, 브랜드명, 상품 가격, 상품 상태, 상품 좋아요 수 정보가 포함된다.")
        @Test
        fun returnsProductWithBrandAndLikes() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct = productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val createdProductLikeCount = productLikeCountJpaRepository.save(
                ProductLikeCountEntityFixture.Companion.aProductLikeCount().productId(createdProduct.id).productLikeCount(20).build(),
            )

            // act
            val productInfo = productFacade.getProduct(createdProduct.id)

            // assert
            assertAll(
                { Assertions.assertThat(productInfo.id).isEqualTo(createdProduct.id) },
                { Assertions.assertThat(productInfo.productName).isEqualTo(createdProduct.name) },
                { Assertions.assertThat(productInfo.brandName).isEqualTo(createdBrand.name) },
                { Assertions.assertThat(productInfo.productPrice).isEqualTo(createdProduct.price.value) },
                { Assertions.assertThat(productInfo.productStatus).isEqualTo(createdProduct.status) },
                {
                    Assertions.assertThat(productInfo.productLikeCount)
                        .isEqualTo(createdProductLikeCount.productLikeCount)
                },
            )
        }
    }

    /*
     **🔗 통합 테스트
    - [ ] 상품 목록은 페이지 번호와 페이지 크기를 기준으로 조회할 수 있다.
    - [ ] 상품 목록은 상품명으로 부분 일치 (Like) 검색할 수 있으며 대소문자를 구분하지 않는다.
    - [ ] 상품 목록은 가격 범위로 검색할 수 있다.
    - [ ] 상품 목록은 브랜드 ID로 검색할 수 있다.
    - [ ] 상품명과 일치하는 상품 목록이 없을 경우 빈 목록을 반환한다.
    - [ ] 상품 목록은 가격 오름차순으로 정렬할 수 있다.
    - [ ] 상품 목록은 가격 내림차순으로 정렬할 수 있다.
    - [ ] 상품 목록은 등록일 오름차순으로 정렬할 수 있다.
    - [ ] 상품 목록은 등록일 내림차순으로 정렬할 수 있다.
    - [ ] 상품 목록은 좋아요 수 오름차순으로 정렬할 수 있다.
    - [ ] 상품 목록은 좋아요 수 내림차순으로 정렬할 수 있다.
    - [ ] 잘못된 정렬 조건이 주어질 경우 400 Bad Request 에러를 반환한다.
     */
    @DisplayName("상품 목록을 검색할 때, ")
    @Nested
    inner class Search {
        @DisplayName("상품 목록은 페이지 번호와 페이지 크기를 기준으로 조회할 수 있다.")
        @Test
        fun returnsProductListByPageAndSize() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { Assertions.assertThat(productsPage).hasSize(2) },
                { Assertions.assertThat(productsPage.totalElements).isEqualTo(2) },
                {
                    Assertions.assertThat(productsPage).extracting("productName")
                        .containsExactlyInAnyOrder("상품A", "상품B")
                },
            )
        }

        @DisplayName("상품 목록은 상품명으로 부분 일치 (Like) 검색할 수 있으며 대소문자를 구분하지 않는다.")
        @Test
        fun returnsProductsByPartialNameSearch() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productFacade.searchProducts(ProductSearchCondition("상품"), pageRequest)

            // assert
            assertAll(
                { Assertions.assertThat(productsPage).hasSize(2) },
                { Assertions.assertThat(productsPage.totalElements).isEqualTo(2) },
                {
                    Assertions.assertThat(productsPage).extracting("productName")
                        .containsExactlyInAnyOrder("상품A", "상품B")
                },
            )
        }

        @DisplayName("상품 목록은 가격 범위로 검색할 수 있다.")
        @Test
        fun returnsProductsByPriceRange() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(
                aProduct().brandId(createdBrand.id).name("상품A").price(
                    Price(1000),
                ).build(),
            )
            productJpaRepository.save(
                aProduct().brandId(createdBrand.id).name("상품B").price(
                    Price(2000),
                ).build(),
            )

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productFacade.searchProducts(
                ProductSearchCondition(
                    minPrice = BigDecimal(500.0),
                    maxPrice = BigDecimal(1500.0),
                ),
                pageRequest,
            )

            // assert
            assertAll(
                { Assertions.assertThat(productsPage).hasSize(1) },
                { Assertions.assertThat(productsPage.totalElements).isEqualTo(1) },
                { Assertions.assertThat(productsPage.content[0].productName).isEqualTo(createdProduct1.name) },
                { Assertions.assertThat(productsPage.content[0].productPrice).isEqualTo(createdProduct1.price.value) },
                { Assertions.assertThat(productsPage.content[0].productStatus).isEqualTo(createdProduct1.status) },
            )
        }

        @DisplayName("상품 목록은 브랜드 ID로 검색할 수 있다.")
        @Test
        fun returnsProductsByBrandId() {
            // arrange
            val createdBrand1 = brandJpaRepository.save(aBrand().name("브랜드A").build())
            val createdBrand2 = brandJpaRepository.save(aBrand().name("브랜드B").build())
            val createdProduct1 = productJpaRepository.save(aProduct().brandId(createdBrand1.id).name("상품A").build())
            productJpaRepository.save(aProduct().brandId(createdBrand2.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productFacade.searchProducts(ProductSearchCondition(brandId = createdBrand1.id), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(1) },
                { assertThat(productsPage.totalElements).isEqualTo(1) },
                { assertThat(productsPage.content[0].brandName).isEqualTo(createdBrand1.name) },
                { assertThat(productsPage.content[0].productName).isEqualTo(createdProduct1.name) },
            )
        }

        @DisplayName("상품명과 일치하는 상품 목록이 없을 경우 빈 목록을 반환한다.")
        @Test
        fun returnsEmptyList_whenNoMatchingProducts() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10)
            val products = productFacade.searchProducts(ProductSearchCondition("nonExistsProduct"), pageRequest)

            // assert
            Assertions.assertThat(products).isEmpty()
        }

        @DisplayName("상품 목록은 가격 오름차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByPriceAsc() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(
                aProduct().brandId(createdBrand.id).name("상품A").price(
                    Price(1000),
                ).build(),
            )
            val createdProduct2 = productJpaRepository.save(
                aProduct().brandId(createdBrand.id).name("상품B").price(
                    Price(2000),
                ).build(),
            )

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").ascending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { Assertions.assertThat(productsPage).hasSize(2) },
                { Assertions.assertThat(productsPage.totalElements).isEqualTo(2) },
                { Assertions.assertThat(productsPage.content[0].productPrice).isEqualTo(createdProduct1.price.value) },
                { Assertions.assertThat(productsPage.content[0].productName).isEqualTo(createdProduct1.name) },
                { Assertions.assertThat(productsPage.content[1].productPrice).isEqualTo(createdProduct2.price.value) },
                { Assertions.assertThat(productsPage.content[1].productName).isEqualTo(createdProduct2.name) },
            )
        }

        @DisplayName("상품 목록은 가격 내림차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByPriceDesc() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(
                aProduct().brandId(createdBrand.id).name("상품A").price(
                    Price(1000),
                ).build(),
            )
            val createdProduct2 = productJpaRepository.save(
                aProduct().brandId(createdBrand.id).name("상품B").price(
                    Price(2000),
                ).build(),
            )

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").descending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { Assertions.assertThat(productsPage).hasSize(2) },
                { Assertions.assertThat(productsPage.totalElements).isEqualTo(2) },
                { Assertions.assertThat(productsPage.content[0].productPrice).isEqualTo(createdProduct2.price.value) },
                { Assertions.assertThat(productsPage.content[0].productName).isEqualTo(createdProduct2.name) },
                { Assertions.assertThat(productsPage.content[1].productPrice).isEqualTo(createdProduct1.price.value) },
                { Assertions.assertThat(productsPage.content[1].productName).isEqualTo(createdProduct1.name) },
            )
        }

        @DisplayName("상품 목록은 등록일 오름차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByCreatedAtAsc() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            Thread.sleep(10)
            val createdProduct2 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").ascending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { Assertions.assertThat(productsPage).hasSize(2) },
                { Assertions.assertThat(productsPage.totalElements).isEqualTo(2) },
                { Assertions.assertThat(productsPage.content[0].productName).isEqualTo(createdProduct1.name) },
                { Assertions.assertThat(productsPage.content[1].productName).isEqualTo(createdProduct2.name) },
            )
        }

        @DisplayName("상품 목록은 등록일 내림차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByCreatedAtDesc() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            Thread.sleep(10)
            val createdProduct2 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { Assertions.assertThat(productsPage).hasSize(2) },
                { Assertions.assertThat(productsPage.totalElements).isEqualTo(2) },
                { Assertions.assertThat(productsPage.content[0].productName).isEqualTo(createdProduct2.name) },
                { Assertions.assertThat(productsPage.content[1].productName).isEqualTo(createdProduct1.name) },
            )
        }

        @DisplayName("상품 목록은 좋아요 수 오름차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByLikesAsc() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            val createdProduct2 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())
            val createdProductLikeCount1 = productLikeCountJpaRepository.save(
                ProductLikeCountEntityFixture.aProductLikeCount().productId(createdProduct1.id).productLikeCount(10).build(),
            )
            val createdProductLikeCount2 = productLikeCountJpaRepository.save(
                ProductLikeCountEntityFixture.aProductLikeCount().productId(createdProduct2.id).productLikeCount(20).build(),
            )

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("likeCount").ascending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { Assertions.assertThat(productsPage).hasSize(2) },
                { Assertions.assertThat(productsPage.totalElements).isEqualTo(2) },
                { Assertions.assertThat(productsPage.content[0].productName).isEqualTo(createdProduct1.name) },
                {
                    Assertions.assertThat(productsPage.content[0].productLikeCount)
                        .isEqualTo(createdProductLikeCount1.productLikeCount)
                },
                { Assertions.assertThat(productsPage.content[1].productName).isEqualTo(createdProduct2.name) },
                {
                    Assertions.assertThat(productsPage.content[1].productLikeCount)
                        .isEqualTo(createdProductLikeCount2.productLikeCount)
                },
            )
        }

        @DisplayName("상품 목록은 좋아요 수 내림차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByLikesDesc() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            val createdProduct2 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())
            val createdProductLikeCount1 = productLikeCountJpaRepository.save(
                ProductLikeCountEntityFixture.Companion.aProductLikeCount().productId(createdProduct1.id).productLikeCount(10).build(),
            )
            val createdProductLikeCount2 = productLikeCountJpaRepository.save(
                ProductLikeCountEntityFixture.Companion.aProductLikeCount().productId(createdProduct2.id).productLikeCount(20).build(),
            )

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("likeCount").descending())
            val productsPage = productFacade.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { Assertions.assertThat(productsPage).hasSize(2) },
                { Assertions.assertThat(productsPage.totalElements).isEqualTo(2) },
                { Assertions.assertThat(productsPage.content[0].productName).isEqualTo(createdProduct2.name) },
                {
                    Assertions.assertThat(productsPage.content[0].productLikeCount)
                        .isEqualTo(createdProductLikeCount2.productLikeCount)
                },
                { Assertions.assertThat(productsPage.content[1].productName).isEqualTo(createdProduct1.name) },
                {
                    Assertions.assertThat(productsPage.content[1].productLikeCount)
                        .isEqualTo(createdProductLikeCount1.productLikeCount)
                },
            )
        }

        @DisplayName("잘못된 정렬 조건이 주어질 경우 400 Bad Request 에러를 반환한다.")
        @Test
        fun throwsBadRequest_whenInvalidSortProperty() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val invalidSortField = "invalidProperty"
            val pageRequest = PageRequest.of(0, 10, Sort.by(invalidSortField).ascending())
            val exception = assertThrows<CoreException> {
                productFacade.searchProducts(ProductSearchCondition(), pageRequest)
            }

            // assert
            Assertions.assertThat(exception).isInstanceOf(CoreException::class.java)
            Assertions.assertThat(exception.message).contains("지원하지 않는 정렬 기준입니다: $invalidSortField")
        }
    }
}
