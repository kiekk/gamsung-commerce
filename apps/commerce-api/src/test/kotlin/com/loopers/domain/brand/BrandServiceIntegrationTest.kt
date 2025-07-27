package com.loopers.domain.brand

import com.loopers.domain.brand.BrandEntityFixture.Companion.aBrand
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BrandServiceIntegrationTest @Autowired constructor(
    private val brandService: BrandService,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
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
        fun failsToCreateBrand_whenBrandNameIsDuplicate() {
            // arrange
            val brandEntity = aBrand().build()
            brandService.createBrand(brandEntity)

            // act
            val exception = assertThrows<CoreException> {
                brandService.createBrand(brandEntity)
            }

            // assert
            assertAll(
                { assertThat(exception.errorType).isEqualTo(ErrorType.CONFLICT) },
                { assertThat(exception.message).contains("이미 존재하는 브랜드입니다: ${brandEntity.name}") },
            )
        }

        @DisplayName("브랜드명, 브랜드상태가 올바른 경우 브랜드를 등록한다.")
        @Test
        fun createsBrand_whenBrandNameAndStatusAreValid() {
            // arrange
            val brandEntity = aBrand().build()

            // act
            val createdBrand = brandService.createBrand(brandEntity)

            // assert
            assertAll(
                { assertThat(createdBrand.id).isNotNull() },
                { assertThat(createdBrand.name).isEqualTo(brandEntity.name) },
                { assertThat(createdBrand.status).isEqualTo(brandEntity.status) },
            )
        }
    }
}
