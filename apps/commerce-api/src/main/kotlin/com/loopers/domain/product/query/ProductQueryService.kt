package com.loopers.domain.product.query

import com.loopers.domain.brand.QBrandEntity
import com.loopers.domain.product.QProductEntity
import com.loopers.domain.productlike.QProductLikeCountEntity
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProductQueryService(
    private val queryFactory: JPAQueryFactory,
) {
    fun searchProducts(condition: ProductSearchCondition, pageable: Pageable): Page<ProductListViewModel> {
        val product = QProductEntity.productEntity
        val brand = QBrandEntity.brandEntity
        val likeCount = QProductLikeCountEntity.productLikeCountEntity

        val predicates = mutableListOf<BooleanExpression?>()

        condition.name?.let {
            predicates.add(product.name.lower().like("${it.lowercase()}%"))
        }

        condition.minPrice?.let {
            predicates.add(product.price.value.goe(it))
        }

        condition.maxPrice?.let {
            predicates.add(product.price.value.loe(it))
        }

        val orders = pageable.sort.mapNotNull { sort ->
            when (sort.property) {
                "name" -> if (sort.isAscending) product.name.asc() else product.name.desc()
                "price" -> if (sort.isAscending) product.price.value.asc() else product.price.value.desc()
                "createdAt" -> if (sort.isAscending) product.createdAt.asc() else product.createdAt.desc()
                "likeCount" -> if (sort.isAscending) likeCount.productLikeCount.asc() else likeCount.productLikeCount.desc()
                else -> null
            }
        }.toTypedArray()

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
            .where(*predicates.filterNotNull().toTypedArray())
            .orderBy(*orders)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val totalCount = queryFactory
            .select(product.count())
            .from(product)
            .join(brand).on(product.brandId.eq(brand.id))
            .leftJoin(likeCount).on(product.id.eq(likeCount.productId))
            .where(*predicates.filterNotNull().toTypedArray())
            .fetchOne() ?: 0L

        return PageImpl(productListViewModels, pageable, totalCount)
    }
}
