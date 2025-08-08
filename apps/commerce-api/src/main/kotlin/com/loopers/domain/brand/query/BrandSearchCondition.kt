package com.loopers.domain.brand.query

import com.loopers.support.enums.brand.BrandStatusType

class BrandSearchCondition(
    val name: String? = null,
    val status: BrandStatusType? = null,
)
