package com.loopers.application.brand

import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.domain.user.UserEntityFixture.Companion.aUser
import com.loopers.infrastructure.brand.BrandJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.support.enums.brand.BrandStatusType
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
class BrandFacadeIntegrationTest @Autowired constructor(
    private val brandFacade: BrandFacade,
    private val brandJpaRepository: BrandJpaRepository,
    private val userJpaRepository: UserJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
) {

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    /*
    **🔗 통합 테스트
    - [ ] 브랜드를 생성할 때, 브랜드가 생성되면 브랜드 정보가 반환된다.
     */
    @DisplayName("브랜드를 생성할 때, ")
    @Nested
    inner class Create {
        @DisplayName("브랜드가 생성되면 브랜드 정보가 반환된다.")
        @Test
        fun createBrand() {
            // arrange
            val createdUser = userJpaRepository.save(aUser().build())
            val criteria = BrandCriteria.Create(
                createdUser.username,
                "TestBrand",
                BrandStatusType.ACTIVE,
            )

            // act
            val response = brandFacade.createBrand(criteria)

            // assert
            assertAll(
                { assertThat(response.name).isEqualTo("TestBrand") },
                { assertThat(response.status).isEqualTo(BrandStatusType.ACTIVE) },
            )
        }
    }

    /*
    **🔗 통합 테스트
    - [ ] 브랜드를 조회할 때, 존재하는 브랜드 ID로 조회하면 브랜드 정보가 반환된다.
    - [ ] 존재하지 않는 브랜드 ID로 조회하면 404 Not Found 예외가 발생한다.
     */
    @DisplayName("브랜드를 조회할 때, ")
    @Nested
    inner class Get {
        @DisplayName("존재하는 브랜드 ID로 조회하면 브랜드 정보가 반환된다.")
        @Test
        fun findBrandByExistingId() {
            // arrange
            val createdBrand = brandJpaRepository.save(aBrand().build())

            // act
            val response = brandFacade.findBrandBy(createdBrand.id)

            // assert
            assertAll(
                { assertThat(response.id).isEqualTo(createdBrand.id) },
                { assertThat(response.name).isEqualTo(createdBrand.name) },
                { assertThat(response.status).isEqualTo(createdBrand.status) },
            )
        }

        @DisplayName("존재하지 않는 브랜드 ID로 조회하면 404 Not Found 예외가 발생한다.")
        @Test
        fun findBrandByNonExistingId() {
            // arrange
            val nonExistentId = 999L

            // act
            val exception = assertThrows<CoreException> {
                brandFacade.findBrandBy(nonExistentId)
            }

            // assert
            assertAll(
                { assertThat(exception).isInstanceOf(CoreException::class.java) },
                { assertThat(exception.message).contains("브랜드를 찾을 수 없습니다. id: $nonExistentId") },
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
    - [ ] 잘못된 정렬 조건이 주어질 경우 400 Bad Request 에러를 반환한다.
     */
    @DisplayName("브랜드 목록을 조회할 때, ")
    @Nested
    inner class Search {
        @DisplayName("브랜드 목록은 페이지 번호와 페이지 크기를 기준으로 조회할 수 있다.")
        @Test
        fun findsBrands_whenPageNumberAndSizeAreValid() {
            // arrange
            repeat(11) { index ->
                brandJpaRepository.save(aBrand().name("브랜드${index + 1}").build())
                Thread.sleep(10)
            }

            // act
            val pageRequest = PageRequest.of(0, 10)
            val brandsPage = brandFacade.searchBrands(BrandCriteria.Query(), pageRequest)

            // assert
            assertAll(
                { assertThat(brandsPage.totalElements).isEqualTo(11) },
                { assertThat(brandsPage.content).hasSize(10) },
                { assertThat(brandsPage.content[0].name).isEqualTo("브랜드1") },
                { assertThat(brandsPage.content[1].name).isEqualTo("브랜드2") },
            )
        }

        @DisplayName("브랜드 목록은 브랜드명으로 부분 일치 (Like) 검색할 수 있으며 대소문자를 구분하지 않는다.")
        @Test
        fun findsBrands_whenSearchingByPartialName() {
            // arrange
            val createdBrand1 = brandJpaRepository.save(aBrand().name("브랜드AAB").build())
            brandJpaRepository.save(aBrand().name("브랜드BA").build())
            val createdBrand3 = brandJpaRepository.save(aBrand().name("브랜드ABB").build())
            val createdBrand4 = brandJpaRepository.save(aBrand().name("브랜드aba").build())

            // act
            val criteria = BrandCriteria.Query(name = "브랜드A")
            val pageRequest = PageRequest.of(0, 10)
            val brandsPage = brandFacade.searchBrands(criteria, pageRequest)

            // assert
            assertAll(
                { assertThat(brandsPage.totalElements).isEqualTo(3) },
                { assertThat(brandsPage.content).hasSize(3) },
                { assertThat(brandsPage.content[0].name).isEqualTo(createdBrand1.name) },
                { assertThat(brandsPage.content[0].status).isEqualTo(createdBrand1.status) },
                { assertThat(brandsPage.content[1].name).isEqualTo(createdBrand3.name) },
                { assertThat(brandsPage.content[1].status).isEqualTo(createdBrand3.status) },
                { assertThat(brandsPage.content[2].name).isEqualTo(createdBrand4.name) },
                { assertThat(brandsPage.content[2].status).isEqualTo(createdBrand4.status) },
            )
        }

        @DisplayName("브랜드 목록은 브랜드 상태로 필터링할 수 있다.")
        @Test
        fun findsBrands_whenFilteringByStatus() {
            // arrange
            val activeBrand = brandJpaRepository.save(aBrand().name("활성브랜드").status(BrandStatusType.ACTIVE).build())
            brandJpaRepository.save(aBrand().name("비활성브랜드").status(BrandStatusType.INACTIVE).build())

            // act
            val criteria = BrandCriteria.Query(status = BrandStatusType.ACTIVE)
            val pageRequest = PageRequest.of(0, 10)
            val brandsPage = brandFacade.searchBrands(criteria, pageRequest)

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
            brandJpaRepository.save(aBrand().name("브랜드A").build())
            brandJpaRepository.save(aBrand().name("브랜드B").build())

            // act
            val criteria = BrandCriteria.Query(name = "브랜드C")
            val pageRequest = PageRequest.of(0, 10)
            val brandsPage = brandFacade.searchBrands(criteria, pageRequest)

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
            val createdBrand1 = brandJpaRepository.save(aBrand().name("브랜드A").build())
            val createdBrand2 = brandJpaRepository.save(aBrand().name("브랜드B").build())

            // act
            val condition = BrandCriteria.Query()
            val pageRequest = PageRequest.of(0, 10, Sort.by("name").ascending())
            val brandsPage = brandFacade.searchBrands(condition, pageRequest)

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
            val createdBrand1 = brandJpaRepository.save(aBrand().name("브랜드A").build())
            val createdBrand2 = brandJpaRepository.save(aBrand().name("브랜드B").build())

            // act
            val condition = BrandCriteria.Query()
            val pageRequest = PageRequest.of(0, 10, Sort.by("name").descending())
            val brandsPage = brandFacade.searchBrands(condition, pageRequest)

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
            val createdBrand1 = brandJpaRepository.save(aBrand().name("브랜드A").build())
            Thread.sleep(10)
            val createdBrand2 = brandJpaRepository.save(aBrand().name("브랜드B").build())

            // act
            val condition = BrandCriteria.Query()
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").ascending())
            val brandsPage = brandFacade.searchBrands(condition, pageRequest)

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
            val createdBrand1 = brandJpaRepository.save(aBrand().name("브랜드A").build())
            val createdBrand2 = brandJpaRepository.save(aBrand().name("브랜드B").build())

            // act
            val condition = BrandCriteria.Query()
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending())
            val brandsPage = brandFacade.searchBrands(condition, pageRequest)

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

        @DisplayName("잘못된 정렬 조건이 주어질 경우 400 Bad Request 에러를 반환한다.")
        @Test
        fun throwsBadRequest_whenInvalidSortProperty() {
            // arrange
            val condition = BrandCriteria.Query()
            val invalidSortField = "invalidProperty"
            val pageRequest = PageRequest.of(0, 10, Sort.by(invalidSortField).ascending())

            // act
            val exception = assertThrows<CoreException> {
                brandFacade.searchBrands(condition, pageRequest)
            }

            // assert
            assertThat(exception.errorType).isEqualTo(ErrorType.BAD_REQUEST)
            assertThat(exception.message).contains("지원하지 않는 정렬 기준입니다: $invalidSortField")
        }
    }

}
