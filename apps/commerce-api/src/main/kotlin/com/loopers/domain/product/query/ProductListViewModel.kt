package com.loopers.domain.product.query

import com.loopers.support.enums.product.ProductStatusType
import com.querydsl.core.annotations.QueryProjection
import java.time.ZonedDateTime

data class ProductListViewModel @QueryProjection constructor(
    val id: Long,
    val name: String,
    val price: Long,
    val productStatus: ProductStatusType,
    val brandName: String,
    val productLikeCount: Int,
    val createdAt: ZonedDateTime,
)
