package com.loopers.domain.brand

import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.support.cache.CacheNames
import com.loopers.support.cache.CacheRepository
import com.loopers.utils.DatabaseCleanUp
import com.loopers.utils.RedisCleanUp
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean

@SpringBootTest
class BrandCacheServiceIntegrationTest @Autowired constructor(
    private val brandJpaRepository: BrandJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
    private val redisCleanUp: RedisCleanUp,
    private val cacheRepository: CacheRepository,
) {
    @MockitoSpyBean
    lateinit var brandRepository: BrandRepository

    @Autowired
    lateinit var brandService: BrandService

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
        redisCleanUp.truncateAll()
    }

    @DisplayName("브랜드 상세 조회 캐시 테스트, ")
    @Nested
    inner class BrandDetailCache {
        @DisplayName("브랜드 상세 조회 시 첫 호출은 DB를 조회하고, 두번째 호출은 캐시를 사용한다.")
        @Test
        fun getBrandById_CacheHit() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())

            // act
            brandService.findBrandBy(createdBrand.id) // 캐시 미스 (DB 조회)
            brandService.findBrandBy(createdBrand.id) // 캐시 히트

            // assert
            verify(brandRepository, times(1)).findById(createdBrand.id)
        }

        @DisplayName("브랜드 상세 조회 시 캐시가 만료되면 다시 DB를 조회한다.")
        @Test
        fun getBrandById_CacheMiss() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())

            // act
            brandService.findBrandBy(createdBrand.id) // 캐시 미스 (DB 조회)
            cacheRepository.evict("${CacheNames.BRAND_DETAIL_V1}${createdBrand.id}") // 캐시 제거
            brandService.findBrandBy(createdBrand.id) // 캐시 미스 (DB 조회)

            // assert
            verify(brandRepository, times(2)).findById(createdBrand.id)
        }
    }
}
