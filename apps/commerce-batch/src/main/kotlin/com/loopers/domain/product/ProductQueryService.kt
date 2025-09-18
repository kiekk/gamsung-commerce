package com.loopers.domain.product

import com.loopers.domain.brand.view.QBrandView
import com.loopers.domain.product.model.ProductListModel
import com.loopers.domain.product.model.QProductListModel
import com.loopers.domain.product.view.QProductView
import com.loopers.domain.productlike.view.QProductLikeCountView
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Service

@Service
class ProductQueryService(
    private val queryFactory: JPAQueryFactory,
) {
    fun getProductsByIds(productIds: List<Long>): List<ProductListModel>? {
        val product = QProductView.productView
        val brand = QBrandView.brandView
        val likeCount = QProductLikeCountView.productLikeCountView

        return queryFactory
            .select(
                QProductListModel(
                    product.id,
                    product.name,
                    product.price,
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
