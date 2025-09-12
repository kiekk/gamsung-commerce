package com.loopers.infrastructure.productrank

import com.loopers.domain.productrank.ProductRankDaily
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRankDailyJpaRepository : JpaRepository<ProductRankDaily, Long>
