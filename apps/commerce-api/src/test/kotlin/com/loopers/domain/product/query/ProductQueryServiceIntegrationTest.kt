package com.loopers.domain.product.query

import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.productlike.ProductLikeCountRepository
import com.loopers.domain.productlike.fixture.ProductLikeCountEntityFixture.Companion.aProductLikeCount
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.math.BigDecimal

@SpringBootTest
class ProductQueryServiceIntegrationTest @Autowired constructor(
    private val productQueryService: ProductQueryService,
    private val productRepository: ProductRepository,
    private val productLikeCountRepository: ProductLikeCountRepository,
    private val brandRepository: BrandRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
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
            val createdBrand = brandRepository.save(aBrand().build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())

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
            val createdBrand = brandRepository.save(aBrand().build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())

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
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").price(Price(1000)).build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").price(Price(2000)).build())

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

        @DisplayName("상품명과 일치하는 상품 목록이 없을 경우 빈 목록을 반환한다.")
        @Test
        fun returnsEmptyList_whenNoMatchingProducts() {
            // arrange
            val createdBrand = brandRepository.save(aBrand().build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())

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
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").price(Price(1000)).build())
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").price(Price(2000)).build())

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
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").price(Price(1000)).build())
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").price(Price(2000)).build())

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
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            Thread.sleep(10)
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())

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
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            Thread.sleep(10)
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())

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
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())
            val createdProductLikeCount1 = productLikeCountRepository.save(aProductLikeCount().productId(createdProduct1.id).productLikeCount(10).build())
            val createdProductLikeCount2 = productLikeCountRepository.save(aProductLikeCount().productId(createdProduct2.id).productLikeCount(20).build())

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
            val createdBrand = brandRepository.save(aBrand().build())
            val createdProduct1 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품A").build())
            val createdProduct2 = productRepository.createProduct(aProduct().brandId(createdBrand.id).name("상품B").build())
            val createdProductLikeCount1 = productLikeCountRepository.save(aProductLikeCount().productId(createdProduct1.id).productLikeCount(10).build())
            val createdProductLikeCount2 = productLikeCountRepository.save(aProductLikeCount().productId(createdProduct2.id).productLikeCount(20).build())

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
    }
}
