package com.loopers.interfaces.api.productrank

import com.loopers.application.productrank.ProductRankCriteria
import com.loopers.application.productrank.ProductRankInfo
import com.loopers.support.enums.product.ProductStatusType
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.LocalDateTime

class ProductRankV1Dto {
    data class Request(
        @field:DateTimeFormat(pattern = "yyyy-MM-dd")
        val rankDate: LocalDate,
    ) {
        fun toCriteria(): ProductRankCriteria.SearchDay {
            return ProductRankCriteria.SearchDay(rankDate)
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
