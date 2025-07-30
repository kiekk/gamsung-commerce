package com.loopers.domain.brand

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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

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
        fun failsToCreateBrand_whenBrandNameAIsDuplicate() {
            // arrange
            val brandCreateCommand = BrandCommand.Create(
                "브랜드A",
                BrandEntity.BrandStatusType.ACTIVE,
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
                BrandEntity.BrandStatusType.ACTIVE,
            )

            // act
            val createdBrand = brandService.createBrand(brandCreateCommand)

            // assert
            assertAll(
                { assertThat(createdBrand.id).isNotNull() },
                { assertThat(createdBrand.name).isEqualTo(brandCreateCommand.name) },
                { assertThat(createdBrand.status).isEqualTo(brandCreateCommand.status) },
            )
        }
    }

    /*
    **🔗 통합 테스트
    - [ ] 브랜드 목록은 페이지 번호와 페이지 크기를 기준으로 조회할 수 있다.
    - [ ] 브랜드 목록은 브랜드명으로 부분 일치 (Like) 검색할 수 있으며 대소문자를 구분하지 않는다.
    - [ ] 브랜드 목록은 브랜드 상태로 필터링할 수 있다.
    - [ ] 브랜드명과 일치하는 브랜드 목록이 없을 경우 빈 목록을 반환한다.
    - [ ] 브랜드 목록은 브랜드명 오름차순으로 정렬할 수 있다.
    - [ ] 브랜드 목록은 브랜드명 내림차순으로 정렬할 수 있다.
    - [ ] 브랜드 목록은 등록일 오름차순으로 정렬할 수 있다.
    - [ ] 브랜드 목록은 등록일 내림차순으로 정렬할 수 있다.
     */
    @DisplayName("브랜드 목록을 조회할 때, ")
    @Nested
    inner class Search {
        @DisplayName("브랜드 목록은 페이지 번호와 페이지 크기를 기준으로 조회할 수 있다.")
        @Test
        fun findsBrands_whenPageNumberAndSizeAreValid() {
            // arrange
            val brandCreateCommand1 = BrandCommand.Create(
                "브랜드A",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val brandCreateCommand2 = BrandCommand.Create(
                "브랜드B",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            brandService.createBrand(brandCreateCommand1)
            brandService.createBrand(brandCreateCommand2)

            // act
            val pageRequest = PageRequest.of(0, 10)
            val brandsPage = brandService.searchBrands(BrandSearchCondition(), pageRequest)

            // assert
            assertAll(
                { assertThat(brandsPage.totalElements).isEqualTo(2) },
                { assertThat(brandsPage.content).hasSize(2) },
                { assertThat(brandsPage.content[0].name).isEqualTo(brandCreateCommand1.name) },
                { assertThat(brandsPage.content[0].status).isEqualTo(brandCreateCommand1.status) },
                { assertThat(brandsPage.content[1].name).isEqualTo(brandCreateCommand2.name) },
                { assertThat(brandsPage.content[1].status).isEqualTo(brandCreateCommand2.status) },
            )
        }

        @DisplayName("브랜드 목록은 브랜드명으로 부분 일치 (Like) 검색할 수 있으며 대소문자를 구분하지 않는다.")
        @Test
        fun findsBrands_whenSearchingByPartialName() {
            // arrange
            val brandCreateCommand1 = BrandCommand.Create(
                "브랜드AAB",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val brandCreateCommand2 = BrandCommand.Create(
                "브랜드BA",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val brandCreateCommand3 = BrandCommand.Create(
                "브랜드ABB",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val brandCreateCommand4 = BrandCommand.Create(
                "브랜드aba",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            brandService.createBrand(brandCreateCommand1)
            brandService.createBrand(brandCreateCommand2)
            brandService.createBrand(brandCreateCommand3)
            brandService.createBrand(brandCreateCommand4)

            // act
            val condition = BrandSearchCondition(name = "브랜드A")
            val pageRequest = PageRequest.of(0, 10)
            val brandsPage = brandService.searchBrands(condition, pageRequest)

            // assert
            assertAll(
                { assertThat(brandsPage.totalElements).isEqualTo(3) },
                { assertThat(brandsPage.content).hasSize(3) },
                { assertThat(brandsPage.content[0].name).isEqualTo(brandCreateCommand1.name) },
                { assertThat(brandsPage.content[0].status).isEqualTo(brandCreateCommand1.status) },
                { assertThat(brandsPage.content[1].name).isEqualTo(brandCreateCommand3.name) },
                { assertThat(brandsPage.content[1].status).isEqualTo(brandCreateCommand3.status) },
                { assertThat(brandsPage.content[2].name).isEqualTo(brandCreateCommand4.name) },
                { assertThat(brandsPage.content[2].status).isEqualTo(brandCreateCommand4.status) },
            )
        }

        @DisplayName("브랜드 목록은 브랜드 상태로 필터링할 수 있다.")
        @Test
        fun findsBrands_whenFilteringByStatus() {
            // arrange
            val activeBrandCreateCommand = BrandCommand.Create(
                "활성브랜드",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val inactiveBrandCreateCommand = BrandCommand.Create(
                "비활성브랜드",
                BrandEntity.BrandStatusType.INACTIVE,
            )
            val activeBrand = brandService.createBrand(activeBrandCreateCommand)
            brandService.createBrand(inactiveBrandCreateCommand)

            // act
            val condition = BrandSearchCondition(status = BrandEntity.BrandStatusType.ACTIVE)
            val pageRequest = PageRequest.of(0, 10)
            val brandsPage = brandService.searchBrands(condition, pageRequest)

            // assert
            assertAll(
                { assertThat(brandsPage.totalElements).isEqualTo(1) },
                { assertThat(brandsPage.content).hasSize(1) },
                { assertThat(brandsPage.content[0].name).isEqualTo(activeBrand.name) },
                { assertThat(brandsPage.content[0].status).isEqualTo(activeBrand.status) },
            )
        }

        @DisplayName("브랜드명과 일치하는 브랜드 목록이 없을 경우 빈 목록을 반환한다.")
        @Test
        fun returnsEmptyList_whenNoBrandsMatchName() {
            // arrange
            val brandCreateCommand1 = BrandCommand.Create(
                "브랜드A",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val brandCreateCommand2 = BrandCommand.Create(
                "브랜드B",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            brandService.createBrand(brandCreateCommand1)
            brandService.createBrand(brandCreateCommand2)

            // act
            val condition = BrandSearchCondition(name = "브랜드C")
            val pageRequest = PageRequest.of(0, 10)
            val brandsPage = brandService.searchBrands(condition, pageRequest)

            // assert
            assertAll(
                { assertThat(brandsPage.totalElements).isEqualTo(0) },
                { assertThat(brandsPage.content).isEmpty() },
            )
        }

        @DisplayName("브랜드 목록은 브랜드명 오름차순으로 정렬할 수 있다.")
        @Test
        fun findsBrands_whenSortingByNameAsc() {
            // arrange
            val brandCreateCommand1 = BrandCommand.Create(
                "브랜드A",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val brandCreateCommand2 = BrandCommand.Create(
                "브랜드B",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val createdBrand1 = brandService.createBrand(brandCreateCommand1)
            val createdBrand2 = brandService.createBrand(brandCreateCommand2)

            // act
            val condition = BrandSearchCondition()
            val pageRequest = PageRequest.of(0, 10, Sort.by("name").ascending())
            val brandsPage = brandService.searchBrands(condition, pageRequest)

            // assert
            assertAll(
                { assertThat(brandsPage.totalElements).isEqualTo(2) },
                { assertThat(brandsPage.content).hasSize(2) },
                { assertThat(brandsPage.content[0].name).isEqualTo(createdBrand1.name) },
                { assertThat(brandsPage.content[0].status).isEqualTo(createdBrand1.status) },
                { assertThat(brandsPage.content[1].name).isEqualTo(createdBrand2.name) },
                { assertThat(brandsPage.content[1].status).isEqualTo(createdBrand2.status) },
            )
        }

        @DisplayName("브랜드 목록은 브랜드명 내림차순으로 정렬할 수 있다.")
        @Test
        fun findsBrands_whenSortingByNameDesc() {
            // arrange
            val brandCreateCommand1 = BrandCommand.Create(
                "브랜드A",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val brandCreateCommand2 = BrandCommand.Create(
                "브랜드B",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val createdBrand1 = brandService.createBrand(brandCreateCommand1)
            val createdBrand2 = brandService.createBrand(brandCreateCommand2)

            // act
            val condition = BrandSearchCondition()
            val pageRequest = PageRequest.of(0, 10, Sort.by("name").descending())
            val brandsPage = brandService.searchBrands(condition, pageRequest)

            // assert
            assertAll(
                { assertThat(brandsPage.totalElements).isEqualTo(2) },
                { assertThat(brandsPage.content).hasSize(2) },
                { assertThat(brandsPage.content[0].name).isEqualTo(createdBrand2.name) },
                { assertThat(brandsPage.content[0].status).isEqualTo(createdBrand2.status) },
                { assertThat(brandsPage.content[1].name).isEqualTo(createdBrand1.name) },
                { assertThat(brandsPage.content[1].status).isEqualTo(createdBrand1.status) },
            )
        }

        @DisplayName("브랜드 목록은 등록일 오름차순으로 정렬할 수 있다.")
        @Test
        fun findsBrands_whenSortingByCreatedAtAsc() {
            // arrange
            val brandCreateCommand1 = BrandCommand.Create(
                "브랜드A",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val brandCreateCommand2 = BrandCommand.Create(
                "브랜드B",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val createdBrand1 = brandService.createBrand(brandCreateCommand1)
            val createdBrand2 = brandService.createBrand(brandCreateCommand2)

            // act
            val condition = BrandSearchCondition()
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").ascending())
            val brandsPage = brandService.searchBrands(condition, pageRequest)

            // assert
            assertAll(
                { assertThat(brandsPage.totalElements).isEqualTo(2) },
                { assertThat(brandsPage.content).hasSize(2) },
                { assertThat(brandsPage.content[0].name).isEqualTo(createdBrand1.name) },
                { assertThat(brandsPage.content[0].status).isEqualTo(createdBrand1.status) },
                { assertThat(brandsPage.content[1].name).isEqualTo(createdBrand2.name) },
                { assertThat(brandsPage.content[1].status).isEqualTo(createdBrand2.status) },
            )
        }

        @DisplayName("브랜드 목록은 등록일 내림차순으로 정렬할 수 있다.")
        @Test
        fun findsBrands_whenSortingByCreatedAtDesc() {
            // arrange
            val brandCreateCommand1 = BrandCommand.Create(
                "브랜드A",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val brandCreateCommand2 = BrandCommand.Create(
                "브랜드B",
                BrandEntity.BrandStatusType.ACTIVE,
            )
            val createdBrand1 = brandService.createBrand(brandCreateCommand1)
            val createdBrand2 = brandService.createBrand(brandCreateCommand2)

            // act
            val condition = BrandSearchCondition()
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending())
            val brandsPage = brandService.searchBrands(condition, pageRequest)

            // assert
            assertAll(
                { assertThat(brandsPage.totalElements).isEqualTo(2) },
                { assertThat(brandsPage.content).hasSize(2) },
                { assertThat(brandsPage.content[0].name).isEqualTo(createdBrand2.name) },
                { assertThat(brandsPage.content[0].status).isEqualTo(createdBrand2.status) },
                { assertThat(brandsPage.content[1].name).isEqualTo(createdBrand1.name) },
                { assertThat(brandsPage.content[1].status).isEqualTo(createdBrand1.status) },
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
            val searchBrands = brandService.searchBrands(BrandSearchCondition(), PageRequest.of(0, 10))
            assertThat(searchBrands).isEmpty()

            // act
            val brand = brandService.findBrandBy(nonExistentBrandId)

            // assert
            assertThat(brand).isNull()
        }

        @DisplayName("브랜드 ID에 해당하는 브랜드가 존재할 경우, 해당 브랜드를 반환한다.")
        @Test
        fun returnsBrand_whenBrandExists() {
            // arrange
            val createdBrand = brandService.createBrand(
                BrandCommand.Create(
                    "브랜드A",
                    BrandEntity.BrandStatusType.ACTIVE,
                ),
            )

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
