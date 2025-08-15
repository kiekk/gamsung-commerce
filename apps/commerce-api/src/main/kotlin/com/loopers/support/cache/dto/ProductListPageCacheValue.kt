package com.loopers.support.cache.dto

import com.loopers.domain.product.query.ProductListViewModel

class ProductListPageCacheValue(
    val content: List<ProductListViewModel>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val sort: String,
)
