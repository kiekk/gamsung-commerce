package com.loopers.domain.product.model

import com.loopers.support.enums.product.ProductStatusType
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class ProductListModel @QueryProjection constructor(
    val id: Long,
    val name: String,
    val price: Long,
    val productStatus: ProductStatusType,
    val brandName: String,
    val productLikeCount: Int,
    val createdAt: LocalDateTime,
)
