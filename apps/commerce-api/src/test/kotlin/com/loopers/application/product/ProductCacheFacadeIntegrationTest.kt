package com.loopers.application.product

import com.loopers.domain.brand.BrandRepository
import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.domain.product.ProductRepository
import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.domain.product.query.ProductQueryService
import com.loopers.domain.product.query.ProductSearchCondition
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.support.KafkaMockConfig
import com.loopers.support.cache.CacheNames
import com.loopers.support.cache.CacheRepository
import com.loopers.utils.DatabaseCleanUp
import com.loopers.utils.RedisCleanUp
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean

@Import(KafkaMockConfig::class)
@SpringBootTest
class ProductCacheFacadeIntegrationTest @Autowired constructor(
    private val brandJpaRepository: BrandJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
    private val redisCleanUp: RedisCleanUp,
    private val cacheRepository: CacheRepository,
) {

    @MockitoSpyBean
    lateinit var productQueryService: ProductQueryService

    @MockitoSpyBean
    lateinit var brandRepository: BrandRepository

    @MockitoSpyBean
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var productFacade: ProductFacade

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
        redisCleanUp.truncateAll()
    }

    @DisplayName("상품 상세 조회 Facade 캐시 테스트, ")
    @Nested
    inner class ProductDetailCache {

        @DisplayName("상품 상세 조회 시 상품, 브랜드 정보 모두 캐시가 존재하지 않으면 DB를 조회하고, 두번째 호출은 캐시를 사용한다.")
        @Test
        fun getProductById_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct = productJpaRepository.save(aProduct().build())

            // act
            productFacade.getProduct(createdProduct.id) // 캐시 미스 (DB 조회)
            productFacade.getProduct(createdProduct.id) // 캐시 히트

            // assert
            verify(brandRepository, times(1)).findById(createdBrand.id)
            verify(productRepository, times(1)).findById(createdProduct.id)
        }

        @DisplayName("상품 상세 조회 시 품, 브랜드 정보 모두 캐시가 만료되면 다시 DB를 조회한다.")
        @Test
        fun getProductById_CacheMiss() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            val createdProduct = productJpaRepository.save(aProduct().build())

            // act
            productFacade.getProduct(createdProduct.id) // 캐시 미스 (DB 조회)
            cacheRepository.evict("${CacheNames.PRODUCT_DETAIL_V1}${createdProduct.id}") // 강제 캐시 제거
            cacheRepository.evict("${CacheNames.BRAND_DETAIL_V1}${createdBrand.id}") // 강제 캐시 제거
            productFacade.getProduct(createdProduct.id) // 캐시 미스 (DB 조회)

            // assert
            verify(brandRepository, times(2)).findById(createdBrand.id)
            verify(productRepository, times(2)).findById(createdProduct.id)
        }
    }

    @DisplayName("상품 목록 조회 Facade 캐시 테스트, ")
    @Nested
    inner class ProductListCache {
        @DisplayName("상품 목록 조회 시 검색 조건이 있으면 캐시에 저장되지 않고 DB를 조회한다.")
        @Test
        fun searchProducts_WithCondition_CacheMiss() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(0, 10)
            val condition = ProductSearchCondition(brandId = createdBrand.id)

            // act
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)

            // assert
            verify(productQueryService, times(2)).searchProducts(
                condition,
                pageRequest,
            )
        }

        @DisplayName("상품 목록 조회 시 1 페이지는 캐시에 저장되어 이후 요청 시 캐시를 사용한다.")
        @Test
        fun searchProducts_Pageable_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(0, 10)
            val condition = ProductSearchCondition()

            // act
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 히트

            // assert
            verify(productQueryService, times(1)).searchProducts(condition, pageRequest)
        }

        @DisplayName("상품 목록 조회 시 2 페이지는 캐시에 저장되어 이후 요청 시 캐시를 사용한다.")
        @Test
        fun searchProducts_PageableSecondPage_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(1, 10)
            val condition = ProductSearchCondition()

            // act
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 히트

            // assert
            verify(productQueryService, times(1)).searchProducts(condition, pageRequest)
        }

        @DisplayName("상품 목록 조회 시 3 페이지부터는 캐시에 저장되지 않고 DB를 조회한다.")
        @Test
        fun searchProducts_PageableThirdPage_CacheMiss() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(2, 10)

            // act
            val condition = ProductSearchCondition()
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)

            // assert
            verify(productQueryService, times(2)).searchProducts(condition, pageRequest)
        }

        @DisplayName("상품 목록 조회 시 등록일 오름차순으로 정렬하면 캐시에 저장되어 이후 요청 시 캐시를 사용한다.")
        @Test
        fun searchProducts_SortedByCreatedAtAsc_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").ascending())
            val condition = ProductSearchCondition()

            // act
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 히트

            // assert
            verify(productQueryService, times(1)).searchProducts(
                condition,
                pageRequest,
            )
        }

        @DisplayName("상품 목록 조회 시 등록일 내림차순으로 정렬하면 캐시에 저장되어 이후 요청 시 캐시를 사용한다.")
        @Test
        fun searchProducts_SortedByCreatedAtDesc_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending())
            val condition = ProductSearchCondition()

            // act
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 히트

            // assert
            verify(productQueryService, times(1)).searchProducts(
                condition,
                pageRequest,
            )
        }

        @DisplayName("상품 목록 조회 시 좋아요 수 내림차순으로 정렬하면 캐시에 저장되어 이후 요청 시 캐시를 사용한다.")
        @Test
        fun searchProducts_SortedByLikeCountDesc_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(0, 10, Sort.by("likeCount").descending())
            val condition = ProductSearchCondition()

            // act
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 히트

            // assert
            verify(productQueryService, times(1)).searchProducts(
                condition,
                pageRequest,
            )
        }

        @DisplayName("상품 목록 조회 시 좋아요 수 오름차순으로 정렬하면 캐시에 저장되어 이후 요청 시 캐시를 사용한다.")
        @Test
        fun searchProducts_SortedByLikeCountAsc_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(0, 10, Sort.by("likeCount").ascending())
            val condition = ProductSearchCondition()

            // act
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 히트

            // assert
            verify(productQueryService, times(1)).searchProducts(
                condition,
                pageRequest,
            )
        }

        @DisplayName("상품 목록 조회 시 가격 내림차순으로 정렬하면 캐시에 저장되어 이후 요청 시 캐시를 사용한다.")
        @Test
        fun searchProducts_SortedByPriceDesc_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").descending())
            val condition = ProductSearchCondition()

            // act
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 히트

            // assert
            verify(productQueryService, times(1)).searchProducts(
                condition,
                pageRequest,
            )
        }

        @DisplayName("상품 목록 조회 시 가격 오름차순으로 정렬하면 캐시에 저장되어 이후 요청 시 캐시를 사용한다.")
        @Test
        fun searchProducts_SortedByPriceAsc_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(0, 10, Sort.by("price").ascending())
            val condition = ProductSearchCondition()

            // act
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 히트

            // assert
            verify(productQueryService, times(1)).searchProducts(
                condition,
                pageRequest,
            )
        }

        @DisplayName("상품 목록 조회 시 상품명 내림차순으로 정렬하면 캐시에 저장되어 이후 요청 시 캐시를 사용한다.")
        @Test
        fun searchProducts_SortedByNameDesc_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(0, 10, Sort.by("name").descending())
            val condition = ProductSearchCondition()

            // act
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 히트

            // assert
            verify(productQueryService, times(1)).searchProducts(
                condition,
                pageRequest,
            )
        }

        @DisplayName("상품 목록 조회 시 상품명 오름차순으로 정렬하면 캐시에 저장되어 이후 요청 시 캐시를 사용한다.")
        @Test
        fun searchProducts_SortedByNameAsc_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())
            productJpaRepository.save(aProduct().brandId(createdBrand.id).build())
            val pageRequest = PageRequest.of(0, 10, Sort.by("name").ascending())
            val condition = ProductSearchCondition()

            // act
            productFacade.searchProducts(condition, pageRequest) // 캐시 미스 (DB 조회)
            productFacade.searchProducts(condition, pageRequest) // 캐시 히트

            // assert
            verify(productQueryService, times(1)).searchProducts(
                condition,
                pageRequest,
            )
        }
    }
}
