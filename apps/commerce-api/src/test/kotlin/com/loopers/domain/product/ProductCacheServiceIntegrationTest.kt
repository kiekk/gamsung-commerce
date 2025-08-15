package com.loopers.domain.product

import com.loopers.domain.product.fixture.ProductEntityFixture.Companion.aProduct
import com.loopers.infrastructure.product.ProductJpaRepository
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
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean

@SpringBootTest
class ProductCacheServiceIntegrationTest @Autowired constructor(
    private val productJpaRepository: ProductJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
    private val redisCleanUp: RedisCleanUp,
    private val cacheRepository: CacheRepository,
) {
    @MockitoSpyBean
    lateinit var productRepository: ProductRepository

    @Autowired
    lateinit var productService: ProductService

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
        redisCleanUp.truncateAll()
    }

    @DisplayName("상품 상세 조회 캐시 테스트, ")
    @Nested
    inner class ProductDetailCache {

        @DisplayName("상품 상세 조회 시 첫 호출은 DB를 조회하고, 두번째 호출은 캐시를 사용한다.")
        @Test
        fun getProductById_CacheHit() {
            // arrange
            val createdProduct = productJpaRepository.save(aProduct().build())

            // act
            productService.findProductBy(createdProduct.id) // 캐시 미스 (DB 조회)
            productService.findProductBy(createdProduct.id) // 캐시 히트

            // assert
            verify(productRepository, times(1)).findById(createdProduct.id)
        }

        @DisplayName("상품 상세 조회 시 캐시가 만료되면 다시 DB를 조회한다.")
        @Test
        // 메서드명은 영어로
        fun getProductById_CacheMiss() {
            // arrange
            val createdProduct = productJpaRepository.save(aProduct().build())

            // act
            productService.findProductBy(createdProduct.id) // 캐시 미스 (DB 조회)
            cacheRepository.evict("${CacheNames.PRODUCT_DETAIL_V1}${createdProduct.id}") // 강제 캐시 제거
            productService.findProductBy(createdProduct.id) // 캐시 미스 (DB 조회)

            // assert
            verify(productRepository, times(2)).findById(createdProduct.id)
        }
    }
}
