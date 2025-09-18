package com.loopers.interfaces.api.productrank

import com.loopers.application.productrank.ProductRankCriteria
import com.loopers.application.productrank.ProductRankInfo
import com.loopers.support.enums.product.ProductStatusType
import com.loopers.support.enums.rank.RankType
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

class ProductRankV1Dto {
    data class Request(
        @field:DateTimeFormat(pattern = "yyyyMMddHH")
        val rankDate: LocalDateTime,
        val rankType: RankType,
    ) {
        fun toCriteria(): ProductRankCriteria.Search {
            return ProductRankCriteria.Search(rankDate, rankType)
        }
    }

    data class ProductRankListResponse(
        val id: Long,
        val productName: String,
        val productPrice: Long,
        val productStatus: ProductStatusType,
        val brandName: String,
        val productLikeCount: Int,
        val createdAt: LocalDateTime,
        val rankNumber: Long? = 0L,
    ) {
        companion object {
            fun from(productRankList: ProductRankInfo.ProductRankList): ProductRankListResponse {
                return ProductRankListResponse(
                    productRankList.id,
                    productRankList.productName,
                    productRankList.productPrice,
                    productRankList.productStatus,
                    productRankList.brandName,
                    productRankList.productLikeCount,
                    productRankList.createdAt,
                    productRankList.rankNumber,
                )
            }
        }
    }
}
