package com.loopers.domain.brand

import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.support.enums.brand.BrandStatusType
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.utils.DatabaseCleanUp
import com.loopers.utils.RedisCleanUp
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
class BrandServiceIntegrationTest @Autowired constructor(
    private val brandService: BrandService,
    private val brandJpaRepository: BrandJpaRepository,
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
    - [ ] 브랜드명이 중복될 경우, 브랜드 등록에 실패한다.
    - [ ] 브랜드명, 브랜드상태가 올바른 경우 브랜드를 등록한다.
     */
    @DisplayName("브랜드를 등록할 떄, ")
    @Nested
    inner class Create {
        @DisplayName("브랜드명이 중복될 경우, 브랜드 등록에 실패한다.")
        @Test
        fun failsToCreateBrand_whenBrandNameAIsDuplicate() {
            // arrange
            val brandCreateCommand = BrandCommand.Create(
                "브랜드A",
            )
            brandService.createBrand(brandCreateCommand)

            // act
            val exception = assertThrows<CoreException> {
                brandService.createBrand(brandCreateCommand)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.CONFLICT) },
                { assertThat(exception.message).contains("이미 존재하는 브랜드입니다: ${brandCreateCommand.name}") },
            )
        }

        @DisplayName("브랜드명, 브랜드상태가 올바른 경우 브랜드를 등록한다.")
        @Test
        fun createsBrand_whenBrandNameAAndStatusAreValid() {
            // arrange
            val brandCreateCommand = BrandCommand.Create(
                "브랜드A",
            )

            // act
            val createdBrand = brandService.createBrand(brandCreateCommand)

            // assert
            assertAll(
                { assertThat(createdBrand.id).isNotNull() },
                { assertThat(createdBrand.name).isEqualTo(brandCreateCommand.name) },
                { assertThat(createdBrand.status).isEqualTo(BrandStatusType.ACTIVE) },
            )
        }
    }

    /*
     **🔗 통합 테스트
    - [ ] 브랜드 ID에 해당하는 브랜드가 존재하지 않을 경우, null을 반환한다.
    - [ ] 브랜드 ID에 해당하는 브랜드가 존재할 경우, 해당 브랜드를 반환한다.
     */
    @DisplayName("브랜드를 조회할 때, ")
    @Nested
    inner class Get {
        @DisplayName("브랜드 ID에 해당하는 브랜드가 존재하지 않을 경우, null을 반환한다.")
        @Test
        fun returnsNull_whenBrandDoesNotExist() {
            // arrange
            val nonExistentBrandId = 999L
            assertThat(brandJpaRepository.findAll()).isEmpty()

            // act
            val brand = brandService.findBrandBy(nonExistentBrandId)

            // assert
            assertThat(brand).isNull()
        }

        @DisplayName("브랜드 ID에 해당하는 브랜드가 존재할 경우, 해당 브랜드를 반환한다.")
        @Test
        fun returnsBrand_whenBrandExists() {
            // arrange
            var createdBrand = brandJpaRepository.save(aBrand().name("브랜드A").build())

            // act
            val findBrand = brandService.findBrandBy(createdBrand.id)

            // assert
            assertAll(
                { assertThat(findBrand).isNotNull() },
                { assertThat(findBrand?.id).isEqualTo(createdBrand.id) },
                { assertThat(findBrand?.name).isEqualTo(createdBrand.name) },
                { assertThat(findBrand?.status).isEqualTo(createdBrand.status) },
            )
        }
    }
}
