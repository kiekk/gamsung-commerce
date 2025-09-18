package com.loopers.domain.product.query

import com.loopers.domain.brand.QBrandEntity
import com.loopers.domain.product.QProductEntity
import com.loopers.domain.productlike.QProductLikeCountEntity
import com.loopers.domain.productlike.QProductLikeEntity
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
class ProductQueryService(
    private val queryFactory: JPAQueryFactory,
) {
    @Transactional(readOnly = true)
    fun searchProducts(condition: ProductSearchCondition, pageable: Pageable): Page<ProductListViewModel> {
        val product = QProductEntity.productEntity
        val brand = QBrandEntity.brandEntity
        val likeCount = QProductLikeCountEntity.productLikeCountEntity

        // 조건 where 절 구성
        val predicate = BooleanBuilder().apply {
            condition.brandId?.let {
                and(product.brandId.eq(it))
            }
            condition.name?.let {
                and(product.name.like("$it%"))
            }
            condition.minPrice?.let {
                and(product.price.value.goe(it))
            }
            condition.maxPrice?.let {
                and(product.price.value.loe(it))
            }
        }

        // 정렬 조건 구성
        val orders = pageable.sort.mapNotNull { sort ->
            when (sort.property) {
                "name" -> if (sort.isAscending) product.name.asc() else product.name.desc()
                "price" -> if (sort.isAscending) product.price.value.asc() else product.price.value.desc()
                "createdAt" -> if (sort.isAscending) product.createdAt.asc() else product.createdAt.desc()
                "likeCount" -> if (sort.isAscending) likeCount.productLikeCount.asc() else likeCount.productLikeCount.desc()
                else -> throw CoreException(ErrorType.BAD_REQUEST, "지원하지 않는 정렬 기준입니다: ${sort.property}")
            }
        }.toTypedArray()

        // 목록 조회
        val productListViewModels = queryFactory
            .select(
                QProductListViewModel(
                    product.id,
                    product.name,
                    product.price.value,
                    product.status,
                    brand.name,
                    likeCount.productLikeCount,
                    product.createdAt,
                ),
            )
            .from(product)
            .join(brand).on(product.brandId.eq(brand.id))
            .leftJoin(likeCount).on(product.id.eq(likeCount.productId))
            .where(predicate)
            .orderBy(*orders)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // 전체 개수 조회
        val totalCount = queryFactory
            .select(product.count())
            .from(product)
            .join(brand).on(product.brandId.eq(brand.id))
            .leftJoin(likeCount).on(product.id.eq(likeCount.productId))
            .where(predicate)
            .fetchOne() ?: 0L

        return PageImpl(productListViewModels, pageable, totalCount)
    }

    @Transactional(readOnly = true)
    fun searchProductsByCountQuery(pageable: Pageable): Page<ProductListViewModel> {
        val product = QProductEntity.productEntity
        val brand = QBrandEntity.brandEntity
        val productLike = QProductLikeEntity.productLikeEntity

        // 목록 조회
        val productListViewModels = queryFactory
            .select(
                QProductListViewModel(
                    product.id,
                    product.name,
                    product.price.value,
                    product.status,
                    brand.name,
                    productLike.id.count().intValue(),
                    product.createdAt,
                ),
            )
            .from(product)
            .join(brand).on(brand.id.eq(product.brandId))
            .leftJoin(productLike).on(productLike.productId.eq(product.id))
            .groupBy(product.id)
            .fetch()

        // 전체 개수 조회
        val totalCount = queryFactory
            .select(product.count())
            .from(product)
            .join(brand).on(product.brandId.eq(brand.id))
            .leftJoin(productLike).on(productLike.productId.eq(product.id))
            .fetchOne() ?: 0L

        return PageImpl(productListViewModels, pageable, totalCount)
    }

    fun getProductsByIds(productIds: List<Long>): List<ProductListViewModel>? {
        val product = QProductEntity.productEntity
        val brand = QBrandEntity.brandEntity
        val likeCount = QProductLikeCountEntity.productLikeCountEntity

        return queryFactory
            .select(
                QProductListViewModel(
                    product.id,
                    product.name,
                    product.price.value,
                    product.status,
                    brand.name,
                    likeCount.productLikeCount,
                    product.createdAt,
                ),
            )
            .from(product)
            .join(brand).on(brand.id.eq(product.brandId))
            .leftJoin(likeCount).on(product.id.eq(likeCount.productId))
            .where(product.id.`in`(productIds))
            .fetch()
    }
}
