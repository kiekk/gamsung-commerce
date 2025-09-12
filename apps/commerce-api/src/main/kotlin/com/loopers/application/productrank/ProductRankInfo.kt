package com.loopers.application.productrank

import com.loopers.domain.product.query.ProductListViewModel
import com.loopers.support.enums.product.ProductStatusType
import java.time.LocalDateTime

class ProductRankInfo {
    data class ProductRankList(
        val id: Long,
        val productName: String,
        val productPrice: Long,
        val productStatus: ProductStatusType,
        val brandName: String,
        val productLikeCount: Int,
        val createdAt: LocalDateTime,
        val rankNumber: Long?,
        val score: Double?,
    ) {
        companion object {
            fun from(productListViewModel: ProductListViewModel, rankNumber: Long?, score: Double?): ProductRankList {
                return ProductRankList(
                    productListViewModel.id,
                    productListViewModel.name,
                    productListViewModel.price,
                    productListViewModel.productStatus,
                    productListViewModel.brandName,
                    productListViewModel.productLikeCount,
                    productListViewModel.createdAt.toLocalDateTime(),
                    rankNumber,
                    score,
                )
            }
        }
    }
}
