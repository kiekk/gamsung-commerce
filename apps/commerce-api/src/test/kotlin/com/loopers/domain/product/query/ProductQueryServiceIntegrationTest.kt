package com.loopers.domain.product.query

import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.productlike.fixture.ProductLikeCountEntityFixture.Companion.aProductLikeCount
import com.loopers.domain.vo.Price
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.productlike.ProductLikeCountJpaRepository
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

@SpringBootTest
class ProductQueryServiceIntegrationTest @Autowired constructor(
    private val productQueryService: ProductQueryService,
    private val productJpaRepository: ProductJpaRepository,
    private val productLikeCountJpaRepository: ProductLikeCountJpaRepository,
    private val brandJpaRepository: BrandJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @MockitoBean
    lateinit var kafkaTemplate: KafkaTemplate<Any, Any>

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
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
    @DisplayName("상품을 검색할 때, ")
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
            val productsPage = productQueryService.searchProducts(ProductSearchCondition(), pageRequest)

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
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productQueryService.searchProducts(ProductSearchCondition("상품"), pageRequest)

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
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").price(Price(1000)).build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").price(Price(2000)).build())

            // act
            val pageRequest = PageRequest.of(0, 10)
            val productsPage = productQueryService.searchProducts(ProductSearchCondition(minPrice = BigDecimal(500.0), maxPrice = BigDecimal(1500.0)), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(1) },
                { assertThat(productsPage.totalElements).isEqualTo(1) },
                { assertThat(productsPage.content[0].name).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[0].price).isEqualTo(createdProduct1.price.value) },
                { assertThat(productsPage.content[0].productStatus).isEqualTo(createdProduct1.status) },
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
            val productsPage = productQueryService.searchProducts(ProductSearchCondition(brandId = createdBrand1.id), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(1) },
                { assertThat(productsPage.totalElements).isEqualTo(1) },
                { assertThat(productsPage.content[0].brandName).isEqualTo(createdBrand1.name) },
                { assertThat(productsPage.content[0].name).isEqualTo(createdProduct1.name) },
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
            val products = productQueryService.searchProducts(ProductSearchCondition("nonExistsProduct"), pageRequest)

            // assert
            assertThat(products).isEmpty()
        }

        @DisplayName("상품 목록은 가격 오름차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByPriceAsc() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").price(Price(1000)).build())
            val createdProduct2 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").price(Price(2000)).build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").ascending())
            val productsPage = productQueryService.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].price).isEqualTo(createdProduct1.price.value) },
                { assertThat(productsPage.content[0].name).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[1].price).isEqualTo(createdProduct2.price.value) },
                { assertThat(productsPage.content[1].name).isEqualTo(createdProduct2.name) },
            )
        }

        @DisplayName("상품 목록은 가격 내림차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByPriceDesc() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").price(Price(1000)).build())
            val createdProduct2 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").price(Price(2000)).build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").descending())
            val productsPage = productQueryService.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].price).isEqualTo(createdProduct2.price.value) },
                { assertThat(productsPage.content[0].name).isEqualTo(createdProduct2.name) },
                { assertThat(productsPage.content[1].price).isEqualTo(createdProduct1.price.value) },
                { assertThat(productsPage.content[1].name).isEqualTo(createdProduct1.name) },
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
            val productsPage = productQueryService.searchProducts(ProductSearchCondition(), pageRequest)

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
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            Thread.sleep(10)
            val createdProduct2 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())

            // kafka mock
            val future = CompletableFuture.completedFuture(mock<SendResult<Any, Any>>())
            whenever(kafkaTemplate.send(any(), any(), any())).thenReturn(future)

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending())
            val productsPage = productQueryService.searchProducts(ProductSearchCondition(), pageRequest)

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
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            val createdProduct2 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())
            val createdProductLikeCount1 = productLikeCountJpaRepository.save(aProductLikeCount().productId(createdProduct1.id).productLikeCount(10).build())
            val createdProductLikeCount2 = productLikeCountJpaRepository.save(aProductLikeCount().productId(createdProduct2.id).productLikeCount(20).build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("likeCount").ascending())
            val productsPage = productQueryService.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].name).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[0].productLikeCount).isEqualTo(createdProductLikeCount1.productLikeCount) },
                { assertThat(productsPage.content[1].name).isEqualTo(createdProduct2.name) },
                { assertThat(productsPage.content[1].productLikeCount).isEqualTo(createdProductLikeCount2.productLikeCount) },
            )
        }

        @DisplayName("상품 목록은 좋아요 수 내림차순으로 정렬할 수 있다.")
        @Test
        fun returnsProductsSortedByLikesDesc() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct1 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            val createdProduct2 = productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())
            val createdProductLikeCount1 = productLikeCountJpaRepository.save(aProductLikeCount().productId(createdProduct1.id).productLikeCount(10).build())
            val createdProductLikeCount2 = productLikeCountJpaRepository.save(aProductLikeCount().productId(createdProduct2.id).productLikeCount(20).build())

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by("likeCount").descending())
            val productsPage = productQueryService.searchProducts(ProductSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(productsPage).hasSize(2) },
                { assertThat(productsPage.totalElements).isEqualTo(2) },
                { assertThat(productsPage.content[0].name).isEqualTo(createdProduct2.name) },
                { assertThat(productsPage.content[0].productLikeCount).isEqualTo(createdProductLikeCount2.productLikeCount) },
                { assertThat(productsPage.content[1].name).isEqualTo(createdProduct1.name) },
                { assertThat(productsPage.content[1].productLikeCount).isEqualTo(createdProductLikeCount1.productLikeCount) },
            )
        }

        @DisplayName("잘못된 정렬 조건이 주어질 경우 400 Bad Request 에러를 반환한다.")
        @Test
        fun throwsBadRequest_whenInvalidSortCondition() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품A").build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).name("상품B").build())
            val invalidSortField = "invalidField"

            // kafka mock
            val future = CompletableFuture.completedFuture(mock<SendResult<Any, Any>>())
            whenever(kafkaTemplate.send(any(), any(), any())).thenReturn(future)

            // act
            val pageRequest = PageRequest.of(0, 10, Sort.by(invalidSortField).ascending())

            // assert
            val exception = assertThrows<CoreException> {
                productQueryService.searchProducts(ProductSearchCondition(), pageRequest)
            }
            assertThat(exception.errorType).isEqualTo(ErrorType.BAD_REQUEST)
            assertThat(exception.message).contains("지원하지 않는 정렬 기준입니다: $invalidSortField")
        }
    }
}
