package com.loopers.domain.brand.query

import com.loopers.domain.brand.fixture.BrandEntityFixture.Companion.aBrand
import com.loopers.infrastructure.brand.BrandJpaRepository
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
import java.util.concurrent.CompletableFuture

@SpringBootTest
class BrandQueryServiceTest @Autowired constructor(
    private val brandQueryService: BrandQueryService,
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
            val brandsPage = brandQueryService.searchBrands(BrandSearchCondition(), pageRequest)

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
            val condition = BrandSearchCondition(name = "브랜드A")
            val pageRequest = PageRequest.of(0, 10)
            val brandsPage = brandQueryService.searchBrands(condition, pageRequest)

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
            val activeBrand = brandJpaRepository.save(aBrand().name("활성브랜드").build())
            brandJpaRepository.save(aBrand().name("비활성브랜드").build().apply { inactive() })

            // act
            val condition = BrandSearchCondition(status = BrandStatusType.ACTIVE)
            val pageRequest = PageRequest.of(0, 10)
            val brandsPage = brandQueryService.searchBrands(condition, pageRequest)

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

            // kafka mock
            val future = CompletableFuture.completedFuture(mock<SendResult<Any, Any>>())
            whenever(kafkaTemplate.send(any(), any(), any())).thenReturn(future)

            // act
            val condition = BrandSearchCondition(name = "브랜드C")
            val pageRequest = PageRequest.of(0, 10)
            val brandsPage = brandQueryService.searchBrands(condition, pageRequest)

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
            val condition = BrandSearchCondition()
            val pageRequest = PageRequest.of(0, 10, Sort.by("name").ascending())
            val brandsPage = brandQueryService.searchBrands(condition, pageRequest)

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

            // kafka mock
            val future = CompletableFuture.completedFuture(mock<SendResult<Any, Any>>())
            whenever(kafkaTemplate.send(any(), any(), any())).thenReturn(future)

            // act
            val condition = BrandSearchCondition()
            val pageRequest = PageRequest.of(0, 10, Sort.by("name").descending())
            val brandsPage = brandQueryService.searchBrands(condition, pageRequest)

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
            val condition = BrandSearchCondition()
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").ascending())
            val brandsPage = brandQueryService.searchBrands(condition, pageRequest)

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
            val condition = BrandSearchCondition()
            val pageRequest = PageRequest.of(0, 10, Sort.by("createdAt").descending())
            val brandsPage = brandQueryService.searchBrands(condition, pageRequest)

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
            val condition = BrandSearchCondition()
            val invalidSortField = "invalidProperty"
            val pageRequest = PageRequest.of(0, 10, Sort.by(invalidSortField).ascending())

            // act
            val exception = assertThrows<CoreException> {
                brandQueryService.searchBrands(condition, pageRequest)
            }

            // assert
            assertThat(exception.errorType).isEqualTo(ErrorType.BAD_REQUEST)
            assertThat(exception.message).contains("지원하지 않는 정렬 기준입니다: $invalidSortField")
        }
    }
}
