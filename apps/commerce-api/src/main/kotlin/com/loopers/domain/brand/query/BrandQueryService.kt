package com.loopers.domain.brand.query

import com.loopers.domain.brand.QBrandEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BrandQueryService(
    private val queryFactory: JPAQueryFactory,
) {
    fun searchBrands(
        condition: BrandSearchCondition,
        pageable: Pageable,
    ): Page<BrandListViewModel> {
        val brand = QBrandEntity.brandEntity

        // 조건 where 절 구성
        val predicate = BooleanBuilder().apply {
            condition.name?.let {
                and(brand.name.lower().like("${it.lowercase()}%"))
            }
            condition.status?.let {
                and(brand.status.eq(it))
            }
        }

        // 정렬 조건 적용
        val orders = pageable.sort.mapNotNull { sort ->
            when (sort.property) {
                "name" -> if (sort.isAscending) brand.name.asc() else brand.name.desc()
                "createdAt" -> if (sort.isAscending) brand.createdAt.asc() else brand.createdAt.desc()
                else -> throw CoreException(ErrorType.BAD_REQUEST, "지원하지 않는 정렬 기준입니다: ${sort.property}")
            }
        }.toTypedArray()

        // 목록 조회
        val brandEntities = queryFactory
            .select(
                QBrandListViewModel(
                    brand.id,
                    brand.name,
                    brand.status,
                ),
            )
            .from(brand)
            .where(
                condition.name?.let {
                    brand.name.lower().like("${it.lowercase()}%")
                },
                condition.status?.let {
                    brand.status.eq(it)
                },
            )
            .where(predicate)
            .orderBy(*orders)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong()).fetch()

        // 전체 개수 조회
        val totalCount = queryFactory
            .select(brand.count())
            .from(brand)
            .where(predicate)
            .fetchOne() ?: 0L

        return PageImpl(brandEntities, pageable, totalCount)
    }
}
