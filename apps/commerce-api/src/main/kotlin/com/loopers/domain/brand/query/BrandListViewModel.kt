package com.loopers.domain.brand.query

import com.loopers.support.enums.brand.BrandStatusType
import com.querydsl.core.annotations.QueryProjection

data class BrandListViewModel @QueryProjection constructor(
    val id: Long,
    val name: String,
    val status: BrandStatusType,
)
